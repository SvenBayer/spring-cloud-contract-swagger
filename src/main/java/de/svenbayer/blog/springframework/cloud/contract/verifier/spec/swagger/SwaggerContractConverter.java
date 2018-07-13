package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import groovy.lang.Closure;
import io.swagger.models.*;
import io.swagger.models.Response;
import io.swagger.models.parameters.*;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.*;
import io.swagger.parser.SwaggerParser;
import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.spec.ContractConverter;
import org.springframework.cloud.contract.spec.internal.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Sven Bayer
 */
public class SwaggerContractConverter implements ContractConverter<Swagger> {

	@Override
	public boolean isAccepted(File file) {
		try {
			Swagger swagger = new SwaggerParser().read(file.getPath());
			return swagger != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Collection<Contract> convertFrom(File file) {
		Swagger swagger = new SwaggerParser().read(file.getPath());
		if (swagger == null || swagger.getPaths() == null) {
			return Collections.emptyList();
		}
		final AtomicInteger priority = new AtomicInteger();
		return swagger.getPaths().entrySet().stream()
				.flatMap(pathEntry -> {
					String pathLink = pathEntry.getKey();
					return pathEntry.getValue().getOperationMap().entrySet().stream()
							.map(operationEntry -> {
								Contract contract = Contract.make(Closure.IDENTITY);
								Operation operation = operationEntry.getValue();
								if (operation.getDescription() != null) {
									contract.description(operation.getDescription());
								}
								if (operation.getTags() != null) {
									contract.setLabel(String.join("_", operation.getTags()));
								}
								if (operation.getSummary() != null) {
									contract.setName(operation.getSummary());
								}
								contract.setPriority(priority.getAndIncrement());
								if (operation.getVendorExtensions() != null && "true".equals(operation.getVendorExtensions().get("x-ignore"))) {
									contract.setIgnored(true);
								}

								final Request request = new Request();
								contract.setRequest(request);

								HttpMethod httpMethod = operationEntry.getKey();
								if (httpMethod != null) {
									request.method(httpMethod.name());
								}
								if (pathLink != null) {
									request.urlPath(swagger.getBasePath() + pathLink);
									// We ignore url() for now and only use urlPath
									if (operation.getParameters() != null) {
										final QueryParameters queryParameters = new QueryParameters();
										request.getUrlPath().setQueryParameters(queryParameters);
										operation.getParameters().stream()
												.filter(param -> param instanceof QueryParameter || param instanceof PathParameter)
												.map(AbstractSerializableParameter.class::cast)
												//TODO maybe collect as map first and then set it
												.forEach(param -> queryParameters.parameter(param.getName(), createDslValueForParameter(param)));
									}
								}

								request.headers(Closure.IDENTITY);
								Headers requestHeaders = request.getHeaders();

								if (operation.getParameters() != null) {
									operation.getParameters().forEach(param -> {
										if (param instanceof HeaderParameter) {
											HeaderParameter headerParameter = HeaderParameter.class.cast(param);
											DslProperty clientValue = createDslValueForParameter(headerParameter);
											if (headerParameter.getName() != null) {
												requestHeaders.header(headerParameter.getName(), clientValue);
											}
										}
										// Cookie parameters are not supported by Swagger 2.0
										if (param instanceof BodyParameter) {
											BodyParameter bodyParameter = BodyParameter.class.cast(param);
											Object value = createDefaultValueForRequestBodyParameter(bodyParameter, swagger.getDefinitions());
											if (value != null) {
												request.body(value);
											}
											BodyMatchers bodyMatchers = new BodyMatchers();
											Map<String, MatchingTypeValue> jsonPaths = createValueForRequestBodyParameter(bodyParameter, swagger.getDefinitions());
											for (Map.Entry<String, MatchingTypeValue> entry : jsonPaths.entrySet()) {
												bodyMatchers.jsonPath(entry.getKey(), entry.getValue());
											}
											request.setBodyMatchers(bodyMatchers);
										}
									});
								}
								if (operation.getConsumes() != null) {
									operation.getConsumes().forEach(requestHeaders::contentType);
								}

								org.springframework.cloud.contract.spec.internal.Response response = new org.springframework.cloud.contract.spec.internal.Response();
								contract.setResponse(response);

								Map.Entry<String, Response> responseEntry = operation.getResponses().entrySet().iterator().next();
								String responseStatus = responseEntry.getKey();
								response.status(Integer.valueOf(responseStatus));

								response.headers(Closure.IDENTITY);
								Headers responseHeaders = response.getHeaders();

								if (responseEntry.getValue().getHeaders() != null) {
									responseEntry.getValue().getHeaders().forEach((key, value) -> {
										if (key != null) {
											DslProperty serverValue = createDslValueForProperty(key, value, swagger.getDefinitions());
											responseHeaders.header(key, serverValue);
										}
									});
								}
								if (operation.getProduces() != null) {
									operation.getProduces().forEach(responseHeaders::contentType);
								}

								// Cookie parameters are not supported by Swagger 2.0 ?
								if (responseEntry.getValue().getResponseSchema() != null) {
									String bodyValue = createValueForResponseBody(responseEntry.getValue(), swagger.getDefinitions());
									response.body(bodyValue);
								}
								// Async / Callback urls not supported yet async()
								// No support for bodyMatchers
								return contract;
							});
				}).collect(Collectors.toList());
	}

	@Override
	public Swagger convertTo(Collection<Contract> contract) {
		// TODO conversion from Spring Cloud Contract to Swagger is not supported yet
		return new Swagger();
	}

	private String createValueForResponseBody(Response response, Map<String, Model> definitions) {
		Object rawValue;
		if (response.getExamples() != null && response.getExamples().values().toArray()[0] != null) {
			rawValue = response.getExamples().values().toArray()[0];
		} else if (response.getVendorExtensions() != null && response.getVendorExtensions().get("x-example") != null) {
			rawValue = response.getVendorExtensions().get("x-example");
		} else if (response.getResponseSchema() != null) {
			String reference = response.getResponseSchema().getReference();
			rawValue = getJsonForPropertiesConstruct(reference, definitions);
		} else {
			throw new IllegalStateException("Could not parse body for response");
		}
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsString(rawValue);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Could not convert raw value for responsoe body to json!", e);
		}
	}

	private DslProperty createDslValueForProperty(String key, Property property, Map<String, Model> definitions) {
		Object value = createValueForProperty(key, property, definitions);
		//TODO avoid default values and set the pattern for the corresponding type
		return new DslProperty(String.valueOf(value));
	}

	private Object createValueForProperty(String key, Property property, Map<String, Model> definitions) {
		if (property.getExample() != null) {
			return postFormatNumericValue(property, property.getExample());
		}
		if (property.getVendorExtensions() != null && property.getVendorExtensions().get("x-example") != null) {
			return postFormatNumericValue(property, property.getVendorExtensions().get("x-example"));
		}
		Object defaultValue = getDefaultValue(property);
		if (defaultValue != null) {
			return defaultValue;
		}
		if (property instanceof RefProperty) {
			RefProperty refProperty = RefProperty.class.cast(property);
			return getJsonForPropertiesConstruct(refProperty.get$ref(), definitions);
		}
		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = ArrayProperty.class.cast(property);
			if (arrayProperty.getItems() == null) {
				return new ArrayList<>(Collections.singleton(1));
			} else {
				return new ArrayList<>(Collections.singletonList(createValueForProperty("", arrayProperty.getItems(), definitions)));
			}
		}
		if (property instanceof AbstractNumericProperty) {
			AbstractNumericProperty numeric = (AbstractNumericProperty) property;
			BigDecimal numericPropertyValue = null;
			if (numeric.getMinimum() != null) {
				if (numeric.getExclusiveMinimum()) {
					numericPropertyValue = numeric.getMinimum().add(new BigDecimal(1));
				} else {
					numericPropertyValue = numeric.getMinimum();
				}
			}
			if (numeric.getMaximum() != null) {
				if (numeric.getExclusiveMaximum() != null) {
					numericPropertyValue = numeric.getMaximum().subtract(new BigDecimal(1));
				} else {
					numericPropertyValue = numeric.getMaximum();
				}
			}
			if (numeric instanceof DoubleProperty || numeric instanceof FloatProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.doubleValue();
				} else {
					return 1.1d;
				}
			}
			if (numeric instanceof LongProperty || numeric instanceof DecimalProperty || numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.longValue();
				} else {
					return 1;
					//TODO return Pattern.compile("[0-9]+");
				}
			}
			return 1;
			//TODO return Pattern.compile("[0-9]+");
		}
		if (property instanceof StringProperty) {
			StringProperty stringProperty = StringProperty.class.cast(property);
			if (stringProperty.getEnum() != null) {
				return stringProperty.getEnum().get(0);
			}
		}
		return key;
		//TODO return new MatchingTypeValue(MatchingType.REGEX, ".+");
	}

	private Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value;
		}
		if (value instanceof Double && (property.getFormat().equals("int32") || property.getFormat().equals("int64"))) {
			return Double.class.cast(value).intValue();
		}
		return value;
	}

	private Object getDefaultValue(Property property) {
		if (property instanceof DoubleProperty) {
			return DoubleProperty.class.cast(property).getDefault();
		}
		if (property instanceof FloatProperty) {
			return FloatProperty.class.cast(property).getDefault();
		}
		if (property instanceof LongProperty) {
			return LongProperty.class.cast(property).getDefault();
		}
		if (property instanceof IntegerProperty) {
			return IntegerProperty.class.cast(property).getDefault();
		}
		if (property instanceof BooleanProperty) {
			return BooleanProperty.class.cast(property).getDefault();
		}
		if (property instanceof StringProperty) {
			return StringProperty.class.cast(property).getDefault();
		}
		return null;
	}

	private Object createDefaultValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
		Object rawValue = null;
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		// TODO this is not verified
		if (param.getExamples() != null && param.getExamples().entrySet().iterator().hasNext()) {
			rawValue = param.getExamples().entrySet().iterator().next();
		} else if (param.getVendorExtensions() != null && param.getVendorExtensions().get("x-example") != null) {
			rawValue = param.getVendorExtensions().get("x-example");
		} else if (param.getSchema() != null) {
			if (param.getSchema().getExample() != null) {
				rawValue = param.getSchema().getExample();
			} else if (param.getSchema().getVendorExtensions() != null && param.getSchema().getVendorExtensions().get("x-example") != null) {
				rawValue = param.getSchema().getVendorExtensions().get("x-example");
			} else if (param.getSchema().getReference() != null) {
				String reference = param.getSchema().getReference();
				Map<?, ?> jsonMap = getJsonForPropertiesConstruct(reference, definitions);

				String result = null;
				try {
					result = mapper.writeValueAsString(jsonMap);
				} catch (JsonProcessingException e) {
					throw new IllegalStateException("Could not parse jsonMap!", e);
				}

				return result;
			}
		} else {
			throw new IllegalStateException("Could not parse body for request");
		}
		try {
			return mapper.writeValueAsString(rawValue);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Could not parse rawValue!", e);
		}
	}

	private Map<String, MatchingTypeValue> createValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
		Map<String, MatchingTypeValue> result = new HashMap<>();
		if (param.getSchema().getReference() != null) {
			String reference = param.getSchema().getReference();
			Map<String, Object> jsonMap = getJsonForPropertiesConstruct(reference, definitions);

			Map<String, Object> jsonPropsWithValues = getJsonPathsWithValues(jsonMap);

			for (Map.Entry<String, Object> jsonPathValue : jsonPropsWithValues.entrySet()) {
				if (jsonPathValue.getValue() instanceof MatchingTypeValue) {
					result.put(jsonPathValue.getKey(), MatchingTypeValue.class.cast(jsonPathValue.getValue()));
				} else {
					result.put(jsonPathValue.getKey(), new MatchingTypeValue(MatchingType.EQUALITY, jsonPathValue.getValue()));
				}
			}
		} else {
			throw new IllegalStateException("Could not parse body for request");
		}
		result.put("1", new MatchingTypeValue(MatchingType.REGEX, ".+"));
		return result;
	}

	List<String> getJsonPaths(String json) {
		Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
		net.minidev.json.JSONArray paths = JsonPath.using(conf).parse(json).read("$..*");
		paths.removeIf(a -> paths.stream().anyMatch(b -> b.toString().startsWith(a.toString()) && b.toString().length() > a.toString().length()));
		return paths.stream()
				.map(Object::toString)
				.collect(Collectors.toList());
	}

	Map<String, Object> getJsonPathsWithValues(Map<String, Object> jsonMap) {
		Map<List<String>, Object> flattenedJsonProperties = getFlattenedJsonProperties(jsonMap);
		return flattenedJsonProperties.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().stream()
						.map(key -> {
							if (key.equals("[*]")) {
								return key;
							} else {
								return "['" + key + "']";
							}
						}).collect(Collectors.joining("", "$", ""))
						, Map.Entry::getValue));
	}

	Map<List<String>, Object> getFlattenedJsonProperties(Map<String, Object> jsonMap) {
		Map<List<String>, Object> expandJsonMap = getExpandJsonMap(jsonMap);
		return getFlattenedJsonPropertiesExpand(expandJsonMap);
	}

	private Map<List<String>, Object> getExpandJsonMap(Map<String, Object> jsonMap) {
		return jsonMap.entrySet().stream()
				.collect(Collectors.toMap(e -> new ArrayList<>(Collections.singletonList(e.getKey())), Map.Entry::getValue));
	}

	private Map<List<String>, Object> getFlattenedJsonPropertiesExpand(Map<List<String>, Object> jsonMap) {
		Map<List<String>, Object> result = new HashMap<>();
		for (Map.Entry<List<String>, Object> entry : jsonMap.entrySet()) {
			ArrayList<String> key = new ArrayList<>(entry.getKey());
			if (entry.getValue() instanceof List) {
				key.add("[*]");
				List subList = List.class.cast(entry.getValue());
				if (subList.get(0) instanceof Map) {
					Map<String, Object> subMap = Map.class.cast(subList.get(0));
					Map<List<String>, Object> expandSubMap = new HashMap<>();
					for (Map.Entry<String, Object> subEntry : subMap.entrySet()) {
						ArrayList<String> cloneKey = ArrayList.class.cast(key.clone());
						cloneKey.add(subEntry.getKey());
						expandSubMap.put(cloneKey, subEntry.getValue());
					}
					Map flattenedJsonProperties = getFlattenedJsonPropertiesExpand(expandSubMap);
					result.putAll(flattenedJsonProperties);
				} else {
					result.put(key, subList.get(0));
				}
			} else if (entry.getValue() instanceof Map) {
				Map<String, Object> subMap = Map.class.cast(entry.getValue());
				Map<List<String>, Object> flattenedJsonProperties = getFlattenedJsonProperties(subMap);
				result.putAll(flattenedJsonProperties);
			} else {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private DslProperty createDslValueForParameter(AbstractSerializableParameter param) {
		Object value = createServerValueForParameter(param);
		if (value == null) {
			value = createDefaultValueForType(param);
			if (param.pattern == null) {
				Pattern pattern = createPatternForDefaultValue(param);
				return new DslProperty(pattern, value);
			}
		}
		DslProperty dslProperty;
		// TODO we need to check if the pattern matches
		if (param.pattern != null) {
			dslProperty = new DslProperty(Pattern.compile(param.pattern), value);
		} else {
			dslProperty = new DslProperty(value);
		}
		return dslProperty;
	}

	private Map<String, Object> getJsonForPropertiesConstruct(String reference, Map<String, Model> definitions) {
		String referenceName = reference.substring(reference.lastIndexOf("/") + 1);
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> createValueForProperty(entry.getKey(), entry.getValue(), definitions)));
	}

	private Object createServerValueForParameter(AbstractSerializableParameter param) {
		if (param.getExample() != null) {
			return param.getExample();
		}
		if (param.getVendorExtensions() != null && param.getVendorExtensions().get("x-example") != null) {
			return param.getVendorExtensions().get("x-example");
		}
		if (param.getDefaultValue() != null) {
			return param.getDefaultValue();
		}
		if (param.getEnum() != null && param.getEnum().get(0) != null) {
			return param.getEnum().get(0);
		}
		return null;
	}

	private Pattern createPatternForDefaultValue(AbstractSerializableParameter param) {
		String regex = createRegexForDefaultValue(param);
		return Pattern.compile(regex);
	}

	private String createRegexForDefaultValue(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if ("string".equals(type)) {
			return ".+";
		}
		if (("number".equals(type)) && ("double".equals(format) || "float".equals(format))) {
			return "[0-9]+\\.[0-9]+";
		}
		if ("number".equals(type)) {
			return "[0-9]+";
		}
		if ("boolean".equals(type)) {
			return "(true|false)";
		}
		return ".+";
	}

	private Object createDefaultValueForType(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if ("string".equals(type)) {
			if (param.getName() != null && !param.getName().isEmpty()) {
				return param.getName();
			} else {
				return "string";
			}
		}
		if (("number".equals(type)) && ("double".equals(format) || "float".equals(format))) {
			return 1.1;
		}
		if ("number".equals(type)) {
			return 1;
		}
		if ("boolean".equals(type)) {
			return true;
		}
		return 1;
	}
}

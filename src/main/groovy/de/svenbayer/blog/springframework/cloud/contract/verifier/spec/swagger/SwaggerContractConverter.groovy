package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import groovy.transform.CompileStatic
import io.swagger.models.Model
import io.swagger.models.Response
import io.swagger.models.Swagger
import io.swagger.models.parameters.*
import io.swagger.models.properties.*
import io.swagger.parser.SwaggerParser
import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.ContractConverter
import org.springframework.cloud.contract.spec.internal.DslProperty
import org.springframework.cloud.contract.spec.internal.MatchingType
import org.springframework.cloud.contract.spec.internal.MatchingTypeValue

import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Sven Bayer
 */
@CompileStatic
class SwaggerContractConverter implements ContractConverter<Swagger> {

	@Override
	boolean isAccepted(File file) {
		try {
			Swagger swagger = new SwaggerParser().read(file.getPath())
			return swagger != null
		} catch (Exception e) {
			return false
		}
	}

	@Override
	Collection<Contract> convertFrom(File file) {
		Swagger swagger = new SwaggerParser().read(file.getPath())
		int i = 0
		Collection<Contract> contracts = new ArrayList<>()
		swagger?.paths?.each { pathLink, pathObject ->
			pathObject.operationMap.each { httpMethod, operation ->
				Contract contract = Contract.make {
					if (operation.description) description(operation.description)
					if (operation.tags) label(operation.tags.join("_"))
					if (operation.summary) name(operation.summary)
					priority(i++)
					if (true == operation.vendorExtensions?.get("x-ignore")) ignored()
					request {
						if (httpMethod) method(httpMethod.name())
						if (pathLink) urlPath(swagger.basePath + pathLink) {
							// We ignore url() for now and only use urlPath
							if (operation.parameters) {
								queryParameters {
									operation.parameters.each { Parameter param ->
										if (param instanceof QueryParameter || param instanceof PathParameter) {
											AbstractSerializableParameter abstractParam = (AbstractSerializableParameter) param
											DslProperty value = createDslValueForParameter(abstractParam)
											parameter(abstractParam.name, value)
										}
									}
								}
							}
						}
						headers {}
						if (operation.parameters) {
							operation.parameters.each { Parameter param ->
								if (param instanceof HeaderParameter) {
									HeaderParameter headerParameter = (HeaderParameter) param
									DslProperty clientValue = createDslValueForParameter(headerParameter)
									if (headerParameter.name != null) {
										getHeaders().header(headerParameter.name, clientValue)
									}
								}
								// Cookie parameters are not supported by Swagger 2.0
								if (param instanceof BodyParameter) {
									BodyParameter bodyParameter = (BodyParameter) param
									Object value = createValueForRequestBodyParameter(bodyParameter, swagger.definitions)
									body(value)
									// TODO body matcher
                                    /*bodyMatchers {
                                        Map<String, MatchingTypeValue> jsonPaths = createJsonPathsForBodyParameter(bodyParameter, swagger.definitions)
                                        jsonPath()
                                    }*/
								}
							}
						}
						if (operation.consumes) {
							operation.consumes.each { if (it) getHeaders().contentType(it) }
						}
					}
					response {
                        Map.Entry<String, Response> responseEntry = operation.responses.entrySet().getAt(0)
                        status(responseEntry.key.toInteger())
                        headers {
                            if (operation.produces) {
                                operation.produces.each { if (it) contentType(it) }
                            }
                            responseEntry.value.headers?.each { String key, Property value ->
                            DslProperty serverValue = createDslValueForProperty(key, value, swagger.definitions)
                            if (key != null) {
									header(key, serverValue)
								}
                            }
						}
						// Cookie parameters are not supported by Swagger 2.0 ?
						if (responseEntry.value.responseSchema) {
							String bodyValue = createValueForResponseBody(responseEntry.value, swagger.definitions)
							body(bodyValue)
						}
						// Async / Callback urls not supported yet async()
						// No support for bodyMatchers

					}
				}
				contracts.add(contract)
			}
		}
		return contracts
	}

	@Override
	Swagger convertTo(Collection<Contract> contract) {
		// TODO conversion from Spring Cloud Contract to Swagger is not supported yet
		return new Swagger()
	}

	private String createValueForResponseBody(Response response, Map<String, Model> definitions) {
		Object rawValue
		if (response.examples?.entrySet()?.getAt(0)?.value) {
			rawValue = response.examples.entrySet().getAt(0).value
		} else if (response.vendorExtensions?.get("x-example")) {
			rawValue = response.vendorExtensions.get("x-example")
		} else if (response.responseSchema) {
			def reference = response.getResponseSchema().reference
			rawValue = getJsonForPropertiesConstruct(reference, definitions)
		} else {
			throw new IllegalStateException("Could not parse body for response")
		}
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
		return mapper.writeValueAsString(rawValue)
	}

	private DslProperty createDslValueForProperty(String key, Property property, Map<String, Model> definitions) {
		String value = createValueForProperty(key, property, definitions)
		return new DslProperty(value)
	}

	private Object createValueForProperty(String key, Property property, Map<String, Model> definitions) {
		if (property.example?.properties?.getAt(0)?.properties != null) {
			return postFormatNumericValue(property, property.example.properties.getAt(0).properties)
		}
		if (property.example != null) {
			return postFormatNumericValue(property, property.example)
		}
		if (property.vendorExtensions?.get("x-example") != null) {
			return postFormatNumericValue(property, property.vendorExtensions.get("x-example"))
		}
		Object defaultValue = getDefaultValue(property)
		if (defaultValue != null) {
			return defaultValue
		}
		if (property instanceof RefProperty) {
			RefProperty refProperty = (RefProperty) property
			return getJsonForPropertiesConstruct(refProperty.get$ref(), definitions)
		}
		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = (ArrayProperty) property
			return new ArrayList<>(Arrays.asList(createValueForProperty("", arrayProperty.items, definitions)))
		}
		if (property instanceof AbstractNumericProperty) {
			AbstractNumericProperty numeric = (AbstractNumericProperty) property
			BigDecimal numericPropertyValue = null
			if (numeric.minimum) {
				if (numeric.exclusiveMinimum) {
					numericPropertyValue = numeric.minimum.add(new BigDecimal(1))
				} else {
					numericPropertyValue = numeric.minimum
				}
			}
			if (numeric.maximum) {
				if (numeric.exclusiveMaximum) {
					numericPropertyValue = numeric.maximum.subtract(new BigDecimal(1))
				} else {
					numericPropertyValue = numeric.maximum
				}
			}
			if (numeric instanceof DoubleProperty || numeric instanceof FloatProperty) {
				if (numericPropertyValue?.intValue() != null) {
					return numericPropertyValue.toDouble()
				} else {
					return 1.1d
				}
			}
			if (numeric instanceof LongProperty || numeric instanceof DecimalProperty || numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
				if (numericPropertyValue?.longValue() != null) {
					return numericPropertyValue.longValue()
				} else {
                    return 1
					//TODO return Pattern.compile("[0-9]+")
				}
			}
            return 1
			//TODO return Pattern.compile("[0-9]+")
		}
		if (property instanceof StringProperty) {
			StringProperty stringProperty = StringProperty.cast(property)
			if (stringProperty.enum?.get(0)) {
				return stringProperty.enum.get(0)
			}
		}
        return key
        //TODO return new MatchingTypeValue(MatchingType.REGEX, ".+")
	}

	private Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value
		}
		if (value instanceof Double && (property.getFormat().equals("int32") || property.getFormat().equals("int64"))) {
			return Double.cast(value).intValue()
		}
		return value
	}

	private Object getDefaultValue(Property property) {
		if (property instanceof DoubleProperty) {
			return DoubleProperty.cast(property).default
		}
		if (property instanceof FloatProperty) {
			return FloatProperty.cast(property).default
		}
		if (property instanceof LongProperty) {
			return LongProperty.cast(property).default
		}
		if (property instanceof IntegerProperty) {
			return IntegerProperty.cast(property).default
		}
		if (property instanceof BooleanProperty) {
			return BooleanProperty.cast(property).default
		}
		if (property instanceof StringProperty) {
			return StringProperty.cast(property).default
		}
	}

    private String createValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
        Object rawValue
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        // TODO this is not verified
        if (param.examples?.entrySet()?.getAt(0)?.value != null) {
            rawValue = param.examples.entrySet().getAt(0).value
        } else if (param.schema?.example != null) {
            // TODO this is not verified
            rawValue = param.schema.example
        } else if (param.vendorExtensions?.get("x-example") != null) {
            rawValue = param.vendorExtensions.get("x-example")
        } else if (param.schema?.vendorExtensions?.get("x-example") != null) {
            rawValue = param.vendorExtensions.get("x-example")
        } else if (param.schema != null) {
            if (param.schema.example != null) {
                rawValue = param.schema.example
            } else if (param.schema.vendorExtensions?.get("x-example") != null) {
                rawValue = param.schema.vendorExtensions.get("x-example")
            } else {
                def reference = param.schema.reference
                Map<?, ?> jsonMap = getJsonForPropertiesConstruct(reference, definitions)

                String result = mapper.writeValueAsString(jsonMap)
                List<String> s = getJsonPaths(result)

                return result
            }
        } else {
            throw new IllegalStateException("Could not parse body for request")
        }
        return mapper.writeValueAsString(rawValue)
    }

    /*Map<List<String>, MatchingTypeValue> getJsonMatchers(Map<List<String>, Object> jsonMap) {
        jsonMap.entrySet().stream()
                .map{
            if (it.value instanceof LinkedHashMap) {
                LinkedHashMap<String, Object> subMap = LinkedHashMap.cast(it.value)
                final String key = it.key
                Map<List<String>, Object> will = subMap.entrySet().stream()
                    .map{
                    [ [key, it.key ], it.value ]
                }
                .collect(Collectors.toMap(Map.Entry.getKey(), Map.Entry.getValue()))
            } else {
                return [ it.key, new MatchingTypeValue(MatchingType.EQUALITY, it.value) ]
            }
        } as Map<List<String>, MatchingTypeValue>
    }*/

    List<String> getJsonPaths(String json) {
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build()
        String paths = JsonPath.using(conf).parse(json).read("\$..*")
        String[] pathArray = paths.substring(2, paths.size() - 2).split("\",\"")
        return Arrays.asList(pathArray)
    }

	private DslProperty createDslValueForParameter(AbstractSerializableParameter param) {
		Object value = createServerValueForParameter(param)
        if (value == null) {
            value = createDefaultValueForType(param)
            if (param.pattern == null) {
                Pattern pattern = createPatternForDefaultValue(param)
                return new DslProperty(pattern, value)
            }
        }
		DslProperty dslProperty
		// TODO we need to check if the pattern matches
		if (param.pattern != null) {
			dslProperty = new DslProperty(Pattern.compile(param.pattern), value)
		} else {
			dslProperty = new DslProperty(value)
		}
		return dslProperty
	}

    private Map<?, ?> getJsonForPropertiesConstruct(String reference, Map<String, Model> definitions) {
		def referenceName = reference.substring(reference.lastIndexOf("/") + 1)
		return definitions.get(referenceName).properties.collectEntries { String key, Property property ->
			Object value = createValueForProperty(key, property, definitions)
			return [key, value]
		}
	}

	private Object createServerValueForParameter(AbstractSerializableParameter param) {
		if (param.example?.properties?.entrySet()?.getAt(0)?.value  != null) {
			return param.example.properties.entrySet().getAt(0).value
		}
        if (param.example != null) {
            return param.example
        }
		if (param.vendorExtensions?.get("x-example") != null) {
			return param.vendorExtensions?.get("x-example")
		}
		if (param.defaultValue  != null) {
			return param.defaultValue
		}
		if (param.getEnum()?.get(0)  != null) {
			return param.getEnum().get(0)
		}
		return null
	}

    private Pattern createPatternForDefaultValue(AbstractSerializableParameter param) {
        String regex = createRegexForDefaultValue(param)
        return Pattern.compile(regex)
    }

    private String createRegexForDefaultValue(AbstractSerializableParameter param) {
        String type = param.type
        String format = param.format

        if ("string".equals(type)) {
            return ".+"
        }
        if (("number".equals(type)) && ("double".equals(format) || "float".equals(format))) {
            return "[0-9]+\\.[0-9]+"
        }
        if ("number".equals(type)) {
            return "[0-9]+"
        }
        if ("boolean".equals(type)) {
            return "(true|false)"
        }
        return ".+"
    }

    private Object createDefaultValueForType(AbstractSerializableParameter param) {
		String type = param.type
		String format = param.format

		if ("string".equals(type)) {
			if (!param.name?.isEmpty()) {
				return param.name
			} else {
				return "string"
			}
		}
		if (("number".equals(type)) && ("double".equals(format) || "float".equals(format))) {
			return 1.1
		}
		if ("number".equals(type)) {
			return 1
		}
		if ("boolean".equals(type)) {
			return true
		}
		return 1
	}
}

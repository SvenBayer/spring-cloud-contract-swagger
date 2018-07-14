package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
							.map(operationEntry -> createContract(swagger, priority, pathLink, operationEntry));
				}).collect(Collectors.toList());
	}

	private Contract createContract(Swagger swagger, AtomicInteger priority, String pathLink, Map.Entry<HttpMethod, Operation> operationEntry) {
		Contract contract = Contract.make(Closure.IDENTITY);
		Operation operation = createMetaData(priority, operationEntry, contract);

		createRequest(swagger, pathLink, operationEntry, contract, operation);

		createResponse(swagger, contract, operation);
		// Async / Callback urls not supported yet async()
		// No support for bodyMatchers
		return contract;
	}

	private Operation createMetaData(AtomicInteger priority, Map.Entry<HttpMethod, Operation> operationEntry, Contract contract) {
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
		return operation;
	}

	private void createResponse(Swagger swagger, Contract contract, Operation operation) {
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
					DslProperty serverValue = ValuePropertyBuilder.createDslValueForProperty(key, value, swagger.getDefinitions());
					responseHeaders.header(key, serverValue);
				}
			});
		}
		if (operation.getProduces() != null) {
			operation.getProduces().forEach(responseHeaders::contentType);
		}

		// Cookie parameters are not supported by Swagger 2.0 ?
		if (responseEntry.getValue().getResponseSchema() != null) {
			String bodyValue = ResponseBodyBuilder.createValueForResponseBody(responseEntry.getValue(), swagger.getDefinitions());
			response.body(bodyValue);
		}
	}

	private void createRequest(Swagger swagger, String pathLink, Map.Entry<HttpMethod, Operation> operationEntry, Contract contract, Operation operation) {
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
						.forEach(param -> queryParameters.parameter(param.getName(), DslValueBuilder.createDslValueForParameter(param)));
			}
		}

		request.headers(Closure.IDENTITY);
		Headers requestHeaders = request.getHeaders();

		if (operation.getParameters() != null) {
			operation.getParameters().forEach(param -> {
				if (param instanceof HeaderParameter) {
					HeaderParameter headerParameter = HeaderParameter.class.cast(param);
					DslProperty clientValue = DslValueBuilder.createDslValueForParameter(headerParameter);
					if (headerParameter.getName() != null) {
						requestHeaders.header(headerParameter.getName(), clientValue);
					}
				}
				// Cookie parameters are not supported by Swagger 2.0
				if (param instanceof BodyParameter) {
					BodyParameter bodyParameter = BodyParameter.class.cast(param);
					Object value = RequestBodyParamBuilder.createDefaultValueForRequestBodyParameter(bodyParameter, swagger.getDefinitions());
					if (value != null) {
						request.body(value);
					}
					/*BodyMatchers bodyMatchers = new BodyMatchers();
					Map<String, MatchingTypeValue> jsonPaths = RequestBodyMatchingBuilder.createValueForRequestBodyParameter(bodyParameter, swagger.getDefinitions());
					for (Map.Entry<String, MatchingTypeValue> entry : jsonPaths.entrySet()) {
						bodyMatchers.jsonPath(entry.getKey(), entry.getValue());
					}
					request.setBodyMatchers(bodyMatchers);*/
				}
			});
		}
		if (operation.getConsumes() != null) {
			operation.getConsumes().forEach(requestHeaders::contentType);
		}
	}

	@Override
	public Swagger convertTo(Collection<Contract> contract) {
		// TODO conversion from Spring Cloud Contract to Swagger is not supported yet
		return new Swagger();
	}
}

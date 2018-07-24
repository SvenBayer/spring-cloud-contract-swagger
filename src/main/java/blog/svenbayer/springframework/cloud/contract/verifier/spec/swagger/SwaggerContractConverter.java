package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.*;
import groovy.lang.Closure;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.*;
import io.swagger.parser.SwaggerParser;
import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.spec.ContractConverter;
import org.springframework.cloud.contract.spec.internal.DslProperty;
import org.springframework.cloud.contract.spec.internal.Headers;
import org.springframework.cloud.contract.spec.internal.QueryParameters;
import org.springframework.cloud.contract.spec.internal.Request;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_IGNORE;

/**
 * @author Sven Bayer
 */
public final class SwaggerContractConverter implements ContractConverter<Swagger> {

	private static final String TAG_SEP = "_";

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
		final AtomicInteger priority = new AtomicInteger(1);
		return swagger.getPaths().entrySet().stream()
				.flatMap(pathEntry -> {
					String pathLink = pathEntry.getKey();
					return pathEntry.getValue().getOperationMap().entrySet().stream()
							.map(operationEntry -> createContract(swagger, priority, pathLink, operationEntry));
				})
				.filter(contract -> !contract.isIgnored())
				.collect(Collectors.toList());
	}

	private Contract createContract(Swagger swagger, AtomicInteger priority, String pathLink, Map.Entry<HttpMethod, Operation> operationEntry) {
		Contract contract = Contract.make(Closure.IDENTITY);
		Operation operation = createMetaData(priority, pathLink, operationEntry, contract);

		createRequest(swagger, pathLink, operationEntry, contract, operation);

		createResponse(swagger, contract, operation);
		// Async / Callback urls not supported yet async()
		// No support for bodyMatchers
		return contract;
	}

	private Operation createMetaData(AtomicInteger priority, String pathLink, Map.Entry<HttpMethod, Operation> operationEntry, Contract contract) {
		Operation operation = operationEntry.getValue();
		String contractName = PathLinkBuilder.createContractName(priority, pathLink, operationEntry.getKey());
		contract.setName(contractName);

		if (operation.getDescription() != null) {
			contract.description(operation.getDescription());
		}
		if (operation.getTags() != null) {
			contract.setLabel(String.join(TAG_SEP, operation.getTags()));
		}

		contract.setPriority(priority.getAndIncrement());
		if (operation.getVendorExtensions() != null && operation.getVendorExtensions().get(X_IGNORE.getField()) != null
				&& Boolean.class.cast(operation.getVendorExtensions().get(X_IGNORE.getField()))) {
			contract.setIgnored(true);
		} else {
			contract.setIgnored(false);
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
			operation.getProduces().forEach(contentType -> {
				if (contentType.equals("*/*")) {
					responseHeaders.contentType("");
				} else {
					responseHeaders.contentType(contentType);
				}
			});
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
				operation.getParameters().stream()
						.filter(param -> param instanceof PathParameter)
						.map(AbstractSerializableParameter.class::cast)
						//TODO This has to become more advanced! We need to check types so we can use 1 for int32 etc.
						.forEach(param -> request.urlPath(request.getUrlPath().getClientValue().toString().replace("{" + param.getName() + "}", param.getName())));
				final QueryParameters queryParameters = new QueryParameters();
				request.getUrlPath().setQueryParameters(queryParameters);
				operation.getParameters().stream()
						.filter(param -> param instanceof QueryParameter)
						.map(AbstractSerializableParameter.class::cast)
						.forEach(param -> {
							DslProperty<Object> value = DslValueBuilder.createDslValueForParameter(param);
							if (value != null) {
								queryParameters.parameter(param.getName(), value);
							}
						});
			}
		}

		request.headers(Closure.IDENTITY);
		Headers requestHeaders = request.getHeaders();

		if (operation.getParameters() != null) {
			operation.getParameters().forEach(param -> {
				if (param instanceof HeaderParameter) {
					HeaderParameter headerParameter = HeaderParameter.class.cast(param);
					DslProperty clientValue = DslValueBuilder.createDslValueForParameter(headerParameter);
					if (clientValue != null && headerParameter.getName() != null) {
						requestHeaders.header(headerParameter.getName(), clientValue);
					}
				}
				// Cookie parameters are not supported by Swagger 2.0
				if (param instanceof BodyParameter) {
					BodyParameter bodyParameter = BodyParameter.class.cast(param);
					String value = RequestBodyParamBuilder.createDefaultValueForRequestBodyParameter(bodyParameter, swagger.getDefinitions());
					if (value != null) {
						request.body(value);
					}
				}
			});
		}
		if (operation.getConsumes() != null) {
			operation.getConsumes().forEach(contentType -> {
				if (contentType.equals("*/*")) {
					requestHeaders.contentType("");
				} else {
					requestHeaders.contentType(contentType);
				}
			});
		}
	}

	@Override
	public Swagger convertTo(Collection<Contract> contract) {
		// TODO conversion from Spring Cloud Contract to Swagger is not supported yet
		return new Swagger();
	}
}

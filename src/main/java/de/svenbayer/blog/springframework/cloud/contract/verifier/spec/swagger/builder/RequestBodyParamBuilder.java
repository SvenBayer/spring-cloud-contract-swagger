package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.Model;
import io.swagger.models.parameters.BodyParameter;

import java.util.Map;

import static de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_EXAMPLE;

/**
 * @author Sven Bayer
 */
public final class RequestBodyParamBuilder {

	private RequestBodyParamBuilder() {
	}

	public static Object createDefaultValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
		Object rawValue = null;
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		// TODO this is not verified
		if (param.getExamples() != null && param.getExamples().entrySet().iterator().hasNext()) {
			rawValue = param.getExamples().entrySet().iterator().next();
		} else if (param.getVendorExtensions() != null && param.getVendorExtensions().get(X_EXAMPLE.getField()) != null) {
			rawValue = param.getVendorExtensions().get(X_EXAMPLE.getField());
		} else if (param.getSchema() != null) {
			if (param.getSchema().getExample() != null) {
				rawValue = param.getSchema().getExample();
			} else if (param.getSchema().getVendorExtensions() != null && param.getSchema().getVendorExtensions().get(X_EXAMPLE.getField()) != null) {
				rawValue = param.getSchema().getVendorExtensions().get(X_EXAMPLE.getField());
			} else if (param.getSchema().getReference() != null) {
				String reference = param.getSchema().getReference();
				Map<?, ?> jsonMap = ValuePropertyBuilder.getJsonForPropertiesConstruct(reference, definitions);

				String result;
				try {
					result = mapper.writeValueAsString(jsonMap);
				} catch (JsonProcessingException e) {
					throw new SwaggerContractConverterException("Could not parse jsonMap!", e);
				}

				return result;
			}
		} else {
			throw new SwaggerContractConverterException("Could not parse body for request");
		}
		try {
			return mapper.writeValueAsString(rawValue);
		} catch (JsonProcessingException e) {
			throw new SwaggerContractConverterException("Could not parse rawValue!", e);
		}
	}
}

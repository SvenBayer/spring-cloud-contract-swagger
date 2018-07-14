package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Model;
import io.swagger.models.parameters.BodyParameter;

import java.util.Map;

public class RequestBodyParamBuilder {

	public static Object createDefaultValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
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
				Map<?, ?> jsonMap = ValuePropertyBuilder.getJsonForPropertiesConstruct(reference, definitions);

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
}

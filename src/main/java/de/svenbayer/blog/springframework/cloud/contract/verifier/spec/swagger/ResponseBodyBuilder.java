package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Model;
import io.swagger.models.Response;

import java.util.Map;

public class ResponseBodyBuilder {

	static String createValueForResponseBody(Response response, Map<String, Model> definitions) {
		Object rawValue;
		if (response.getExamples() != null && response.getExamples().values().toArray()[0] != null) {
			rawValue = response.getExamples().values().toArray()[0];
		} else if (response.getVendorExtensions() != null && response.getVendorExtensions().get("x-example") != null) {
			rawValue = response.getVendorExtensions().get("x-example");
		} else if (response.getResponseSchema() != null) {
			String reference = response.getResponseSchema().getReference();
			rawValue = ValuePropertyBuilder.getJsonForPropertiesConstruct(reference, definitions);
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
}

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Model;
import io.swagger.models.Response;

import java.util.Map;

/**
 * Creates the value for a response body.
 *
 * @author Sven Bayer
 */
public final class ResponseBodyBuilder {

	private ResponseBodyBuilder() {
	}

	/**
	 * Creates the value for a response body and given Swagger model definitions.
	 *
	 * @param response the Swagger response
	 * @param definitions the Swagger model definitions
	 * @return the value for the response body
	 */
	public static String createValueForResponseBody(Response response, Map<String, Model> definitions) {
		if (response.getExamples() != null && response.getExamples().values().toArray()[0] != null) {
			return String.valueOf(response.getExamples().values().toArray()[0]);
		} else if (response.getVendorExtensions() != null && response.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.field()) != null) {
			return String.valueOf(response.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.field()));
		} else if (response.getResponseSchema() != null) {
			String reference = response.getResponseSchema().getReference();
			Object rawValue = ResponseHeaderValueBuilder.getJsonForPropertiesConstruct(reference, definitions);
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			try {
				return mapper.writeValueAsString(rawValue);
			} catch (JsonProcessingException e) {
				throw new SwaggerContractConverterException("Could not convert raw value for responsoe body to json!", e);
			}
		} else {
			throw new SwaggerContractConverterException("Could not parse body for response");
		}
	}
}

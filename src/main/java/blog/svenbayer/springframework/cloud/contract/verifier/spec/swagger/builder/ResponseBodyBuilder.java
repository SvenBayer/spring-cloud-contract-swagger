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
 * @author Sven Bayer
 */
public final class ResponseBodyBuilder {

	private ResponseBodyBuilder() {
	}

	public static String createValueForResponseBody(Response response, Map<String, Model> definitions) {
		if (response.getExamples() != null && response.getExamples().values().toArray()[0] != null) {
			return String.valueOf(response.getExamples().values().toArray()[0]);
		} else if (response.getVendorExtensions() != null && response.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()) != null) {
			return String.valueOf(response.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()));
		} else if (response.getResponseSchema() != null) {
			String reference = response.getResponseSchema().getReference();
			Object rawValue = ValuePropertyBuilder.getJsonForPropertiesConstruct(reference, definitions);
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

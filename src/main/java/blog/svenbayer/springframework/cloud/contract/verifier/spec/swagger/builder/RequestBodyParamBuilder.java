package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Model;
import io.swagger.models.parameters.BodyParameter;

import java.util.Map;

/**
 * @author Sven Bayer
 */
public final class RequestBodyParamBuilder {

	private RequestBodyParamBuilder() {
	}

	public static String createDefaultValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
		// TODO this is not verified
		if (param.getExamples() != null && param.getExamples().entrySet().iterator().hasNext()) {
			return String.valueOf(param.getExamples().entrySet().iterator().next());
		} else if (param.getVendorExtensions() != null && param.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()) != null) {
			return String.valueOf(param.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()));
		} else if (param.getSchema() != null) {
			if (param.getSchema().getExample() != null) {
				return String.valueOf(param.getSchema().getExample());
			} else if (param.getSchema().getVendorExtensions() != null && param.getSchema().getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()) != null) {
				return String.valueOf(param.getSchema().getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()));
			} else if (param.getSchema().getReference() != null) {
				String reference = param.getSchema().getReference();
				Map<?, ?> jsonMap = ValuePropertyBuilder.getJsonForPropertiesConstruct(reference, definitions);

				String result;
				try {
					ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
					result = mapper.writeValueAsString(jsonMap);
				} catch (JsonProcessingException e) {
					throw new SwaggerContractConverterException("Could not parse jsonMap!", e);
				}

				return result;
			}
		} else {
			throw new SwaggerContractConverterException("Could not parse body for request");
		}
		return null;
	}
}

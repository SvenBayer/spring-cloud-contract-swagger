package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import io.swagger.models.Model;
import io.swagger.models.parameters.BodyParameter;
import org.springframework.cloud.contract.spec.internal.MatchingType;
import org.springframework.cloud.contract.spec.internal.MatchingTypeValue;

import java.util.HashMap;
import java.util.Map;

public class RequestBodyMatchingBuilder {

	static Map<String, MatchingTypeValue> createValueForRequestBodyParameter(BodyParameter param, Map<String, Model> definitions) {
		Map<String, MatchingTypeValue> result = new HashMap<>();
		if (param.getSchema().getReference() != null) {
			String reference = param.getSchema().getReference();
			Map<String, Object> jsonMap = ValuePropertyBuilder.getJsonForPropertiesConstruct(reference, definitions);

			Map<String, Object> jsonPropsWithValues = JsonPathMatchingBuilder.getJsonPathsWithValues(jsonMap);

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
}

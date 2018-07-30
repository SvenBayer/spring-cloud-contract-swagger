package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.ResponseHeaderValueBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sven Bayer
 */
public class SwaggerDefinitionsRefResolverSwagger implements SwaggerReferenceResolver {

	private static final Map<String, String> REPLACEMENTS = new HashMap<>();

	static {
		REPLACEMENTS.put("\\\\n", "\n");
		REPLACEMENTS.put("\\\\\"", "\"");
		REPLACEMENTS.put("\"\\{", "{");
		REPLACEMENTS.put("\\}\"", "}");
	}

	private String reference;

	public SwaggerDefinitionsRefResolverSwagger(String reference) {
		this.reference = reference;
	}

	/**
	 * Creats a key-value representation for the given reference and Swagger model definitions.
	 *
	 * @param definitions the Swagger model definitions
	 * @return a json representation of the Swagger model definition
	 */
	@Override
	public String resolveReference(Map<String, Model> definitions) {
		Map<String, Object> jsonMap = resolveDefinitionsRef(this.reference, definitions);
		try {
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			String jsonString = mapper.writeValueAsString(jsonMap);
			String cleanJson = cleanupJson(jsonString);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(cleanJson));
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not parse jsonMap!", e);
		}
	}

	private String cleanupJson(String jsonString) {
		for (Map.Entry<String, String> repl : REPLACEMENTS.entrySet()){
			jsonString = jsonString.replaceAll(repl.getKey(), repl.getValue());
		}
		return jsonString;
	}

	private Map<String, Object> resolveDefinitionsRef(String reference, Map<String, Model> definitions) {
		String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> ResponseHeaderValueBuilder.createResponseHeaderValue(entry.getKey(), entry.getValue(), definitions)));
	}
}

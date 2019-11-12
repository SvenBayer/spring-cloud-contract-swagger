package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.ResponseHeaderValueBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sven Bayer
 */
public class SwaggerDefinitionsRefResolverSwagger implements SwaggerReferenceResolver {

	private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<>();

	private ResponseHeaderValueBuilder responseHeaderValueBuilder = new ResponseHeaderValueBuilder();

	static {
		REPLACEMENTS.put("\\\\r\\\\n", System.lineSeparator());
		REPLACEMENTS.put("\\\\n", "\n");
		REPLACEMENTS.put("\\\\r", "\r");
		REPLACEMENTS.put("\\\\\"", "\"");
		REPLACEMENTS.put("\"\\{", "{");
		REPLACEMENTS.put("\\}\"", "}");
	}

	private String reference;

	public SwaggerDefinitionsRefResolverSwagger(String reference) {
		this.reference = reference;
	}

	/**
	 * Creates a key-value representation for the given reference and Swagger model definitions.
	 *
	 * @param definitions the Swagger model definitions
	 * @return a json representation of the Swagger model definition
	 */
	@Override
	public String resolveReference(Map<String, Model> definitions) {
		Object definition = resolveDefinitionsRef(this.reference, definitions);
		try {
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			String jsonString = mapper.writeValueAsString(definition);
			String cleanJson = cleanupJson(jsonString);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(cleanJson));
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not parse jsonMap!", e);
		}
	}

	/**
	 * Cleans up a json-field Json string.
	 *
	 * @param jsonString the Json string that got mapped to often by ObjectMapper
	 * @return the cleaned-up Json string
	 */
	private String cleanupJson(String jsonString) {
		for (Map.Entry<String, String> repl : REPLACEMENTS.entrySet()) {
			jsonString = jsonString.replaceAll(repl.getKey(), repl.getValue());
		}
		return jsonString;
	}

	/**
	 * Resolves a Swagger reference with a given Swagger definitions.
	 * Resolves only object and array references.
	 * @param reference   the swagger object/array reference
	 * @param definitions the Swagger definitions
	 * @return the representation of the Swagger reference
	 */
	private  Object resolveDefinitionsRef(String reference, Map<String, Model> definitions)
	{
		String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		if (definitions == null || definitions.get(referenceName) == null) {
			throw new SwaggerContractConverterException("Reference '" + reference + "' does not exist in definitions");
		}

		if (definitions.get(referenceName) instanceof ArrayModel)
		{
			return resolveArrayDefinitionsRef(reference, definitions);
		}
		else
		{
			return resolveObjectDefinitionsRef(reference, definitions);
		}
	}

	/**
	 * Resolves a Swagger object reference with the given Swagger definitions.
	 *
	 * @param reference   the Swagger object reference
	 * @param definitions the Swagger definitions
	 * @return the key-value representation of the Swagger reference
	 */
	private Map<String, Object> resolveObjectDefinitionsRef(String reference, Map<String, Model> definitions) {
		String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		if (definitions.get(referenceName).getProperties() == null) {
			throw new SwaggerContractConverterException("The object '" + reference + "' does not have properties");
		}
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> this.responseHeaderValueBuilder.createResponseHeaderValue(entry.getKey(), entry.getValue(), definitions)));
	}

	/**
	 * Resolves a Swagger array reference with the given Swagger definitions.
	 * @param reference   the Swagger array reference
	 * @param definitions the Swagger definitions
	 * @return the list representation of the Swagger reference
	 */
	private List<Object> resolveArrayDefinitionsRef(String reference, Map<String, Model> definitions)
	{
		String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		Property items = ((ArrayModel)definitions.get(referenceName)).getItems();

		if (items == null) {
			throw new SwaggerContractConverterException("The array '" + reference + "' does not have items");
		}

		Object responseHeaderValue = this.responseHeaderValueBuilder
				.createResponseHeaderValue(definitions.get(referenceName).getTitle(), items, definitions);
		return Collections.singletonList(responseHeaderValue);
	}
}

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Compares two Jsons if they have the same schema.
 *
 * @author Sven Bayer
 */
public class JsonSchemaComparing {

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Compares two Jsons if they have the same schema. Does this not already exist? Are we re-inventing the wheel???
	 *
	 * @param expectedJson the expected Json
	 * @param actualJson the actual Json
	 * @return true if the Json schemas are equal
	 */
	public boolean isEquals(String expectedJson, String actualJson) {
		JsonNode expectedNode;
		JsonNode actualNode;
		if (expectedJson == null) {
			throw new SwaggerContractConverterException("JSON of Swagger definitions must not be null!");
		}
		if (actualJson == null) {
			throw new SwaggerContractConverterException("JSON file must not be null!");
		}
		try {
			expectedNode = this.mapper.readTree(expectedJson);
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not parse JSON of Swagger definitions!");
		}
		try {
			actualNode = this.mapper.readTree(actualJson);
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not parse JSON of file!");
		}
		return isEquals(expectedNode, actualNode);
	}

	/**
	 * Compares two JsonNodes if their schema is equal.
	 *
	 * @param expectedJsonSchema the expected JsonNode
	 * @param actualJsonSchema the actual JsonNode
	 * @return true if the schema of both JsonNodes is equal
	 */
	private boolean isEquals(JsonNode expectedJsonSchema, JsonNode actualJsonSchema) {
		Set<JsonKeyValuePair> expectedJsonMap = mapNode("root", expectedJsonSchema);
		Set<JsonKeyValuePair> actualJsonMap = mapNode("root", actualJsonSchema);
		return expectedJsonMap.containsAll(actualJsonMap);
	}

	/**
	 * Maps a JsonNode to a key-value representation that ignores values.
	 *
	 * @param name the name of the key
	 * @param node the Json node value
	 * @return the mapped key-value Set of the key and Json node value
	 */
	private Set<JsonKeyValuePair> mapNode(String name, JsonNode node) {
		Set<JsonKeyValuePair> elements = new HashSet<>();
		Iterator<Map.Entry<String, JsonNode>> iteratorFields = node.fields();
		if (!iteratorFields.hasNext()) {
			Iterator<JsonNode> iteratorElements = node.elements();
			while (iteratorElements.hasNext()) {
				JsonNode next = iteratorElements.next();
				if (!(next instanceof ValueNode)) {
					elements.add(new JsonKeyValuePair(name, mapNode(name, next)));
				}
			}
		}
		while (iteratorFields.hasNext()) {
			Map.Entry<String, JsonNode> next = iteratorFields.next();
			JsonNode value = next.getValue();
			if (value instanceof ValueNode) {
				Set<JsonKeyValuePair> subKeyAsValue= new HashSet<>();
				subKeyAsValue.add(new JsonKeyValuePair(next.getKey(), null));
				elements.add(new JsonKeyValuePair(name, subKeyAsValue));
			} else {
				Set<JsonKeyValuePair> subValueForKey = mapNode(next.getKey(), value);
				elements.add(new JsonKeyValuePair(name, subValueForKey));
			}
		}
		return elements;
	}
}

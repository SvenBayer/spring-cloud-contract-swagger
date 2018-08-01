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
 * @author Sven Bayer
 */
public class JsonSchemaComparer {

	private ObjectMapper mapper = new ObjectMapper();

	public boolean isEquals(String expectedJson, String actualJson) {
		JsonNode expectedNode;
		JsonNode actualNode;
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

	private boolean isEquals(JsonNode expectedJsonSchema, JsonNode actualJsonSchema) {
		Set<JsonKeyValuePair> expectedJsonMap = mapNode("root", expectedJsonSchema);
		Set<JsonKeyValuePair> actualJsonMap = mapNode("root", actualJsonSchema);
		return expectedJsonMap.containsAll(actualJsonMap);
	}

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

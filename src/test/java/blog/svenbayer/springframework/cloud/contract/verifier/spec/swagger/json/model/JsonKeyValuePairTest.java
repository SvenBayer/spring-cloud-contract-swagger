package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json.model;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class JsonKeyValuePairTest {

	@DisplayName("Equals for same key and value")
	@Test
	public void equalsForKeyValue() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		HashSet<JsonKeyValuePair> bSet = new HashSet<>();
		bSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair b = new JsonKeyValuePair("a", bSet);

		assertEquals(a, b);
	}
}
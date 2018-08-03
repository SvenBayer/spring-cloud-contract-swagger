package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json.model;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JsonKeyValuePairTest {

	@DisplayName("Equals for same instance")
	@Test
	public void equalsForSameInstance() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		assertEquals(a, a);
	}

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

	@DisplayName("Not equals for same key but different value")
	@Test
	public void notEqualsForKeyDifferentValue() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		HashSet<JsonKeyValuePair> bSet = new HashSet<>();
		bSet.add(new JsonKeyValuePair("different", null));
		JsonKeyValuePair b = new JsonKeyValuePair("a", bSet);

		assertNotEquals(a, b);
	}

	@DisplayName("Not equals for different types")
	@Test
	public void notEqualsForDifferentTypes() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		assertNotEquals(a, "hello");
	}
}
package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json;

import java.util.Objects;
import java.util.Set;

public class JsonKeyValuePair {

	private String key;
	private Set<JsonKeyValuePair> value;

	public JsonKeyValuePair(String key, Set<JsonKeyValuePair> value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JsonKeyValuePair)) return false;
		JsonKeyValuePair that = (JsonKeyValuePair) o;
		return Objects.equals(key, that.key) &&
				Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		//return Objects.hash(key, value);
		int valueHashCode = 0;
		if (value != null) {
			valueHashCode = value.stream()
					.mapToInt(Object::hashCode)
					.sum();
		}
		return Objects.hashCode(key) + valueHashCode;
	}

	@Override
	public String toString() {
		return "JsonKeyValuePair{" +
				"key='" + key + '\'' +
				", value=" + value +
				'}';
	}
}

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
		return Objects.equals(this.key, that.key) &&
				Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		int valueHashCode = 0;
		if (this.value != null) {
			valueHashCode = this.value.stream()
					.mapToInt(Object::hashCode)
					.sum();
		}
		return Objects.hashCode(this.key) + valueHashCode;
	}

	@Override
	public String toString() {
		return "JsonKeyValuePair{" +
				"key='" + this.key + '\'' +
				", value=" + this.value +
				'}';
	}
}

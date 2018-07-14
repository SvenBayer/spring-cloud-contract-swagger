package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import java.util.*;
import java.util.stream.Collectors;

public class JsonPathMatchingBuilder {

	static Map<String, Object> getJsonPathsWithValues(Map<String, Object> jsonMap) {
		Map<List<String>, Object> flattenedJsonProperties = getFlattenedJsonProperties(jsonMap);
		return flattenedJsonProperties.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().stream()
								.map(key -> {
									if (key.equals("[*]")) {
										return key;
									} else {
										return "['" + key + "']";
									}
								}).collect(Collectors.joining("", "$", ""))
						, Map.Entry::getValue));
	}

	static Map<List<String>, Object> getFlattenedJsonProperties(Map<String, Object> jsonMap) {
		Map<List<String>, Object> expandJsonMap = getExpandJsonMap(jsonMap);
		return getFlattenedJsonPropertiesExpand(expandJsonMap);
	}

	private static Map<List<String>, Object> getExpandJsonMap(Map<String, Object> jsonMap) {
		return jsonMap.entrySet().stream()
				.collect(Collectors.toMap(e -> new ArrayList<>(Collections.singletonList(e.getKey())), Map.Entry::getValue));
	}

	private static Map<List<String>, Object> getFlattenedJsonPropertiesExpand(Map<List<String>, Object> jsonMap) {
		Map<List<String>, Object> result = new HashMap<>();
		for (Map.Entry<List<String>, Object> entry : jsonMap.entrySet()) {
			ArrayList<String> key = new ArrayList<>(entry.getKey());
			if (entry.getValue() instanceof List) {
				key.add("[*]");
				List subList = List.class.cast(entry.getValue());
				if (subList.get(0) instanceof Map) {
					Map<String, Object> subMap = Map.class.cast(subList.get(0));
					Map<List<String>, Object> expandSubMap = new HashMap<>();
					for (Map.Entry<String, Object> subEntry : subMap.entrySet()) {
						ArrayList<String> cloneKey = ArrayList.class.cast(key.clone());
						cloneKey.add(subEntry.getKey());
						expandSubMap.put(cloneKey, subEntry.getValue());
					}
					Map flattenedJsonProperties = getFlattenedJsonPropertiesExpand(expandSubMap);
					result.putAll(flattenedJsonProperties);
				} else {
					result.put(key, subList.get(0));
				}
			} else if (entry.getValue() instanceof Map) {
				Map<String, Object> subMap = Map.class.cast(entry.getValue());
				Map<List<String>, Object> flattenedJsonProperties = getFlattenedJsonProperties(subMap);
				result.putAll(flattenedJsonProperties);
			} else {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}
}

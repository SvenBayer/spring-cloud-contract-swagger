package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import io.swagger.models.Model;
import io.swagger.models.properties.*;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class ValuePropertyBuilder {

	static DslProperty createDslValueForProperty(String key, Property property, Map<String, Model> definitions) {
		Object value = createValueForProperty(key, property, definitions);
		//TODO avoid default values and set the pattern for the corresponding type
		return new DslProperty(String.valueOf(value));
	}

	static Object createValueForProperty(String key, Property property, Map<String, Model> definitions) {
		if (property.getExample() != null) {
			return postFormatNumericValue(property, property.getExample());
		}
		if (property.getVendorExtensions() != null && property.getVendorExtensions().get("x-example") != null) {
			return postFormatNumericValue(property, property.getVendorExtensions().get("x-example"));
		}
		Object defaultValue = getDefaultValue(property);
		if (defaultValue != null) {
			return defaultValue;
		}
		if (property instanceof RefProperty) {
			RefProperty refProperty = RefProperty.class.cast(property);
			return getJsonForPropertiesConstruct(refProperty.get$ref(), definitions);
		}
		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = ArrayProperty.class.cast(property);
			if (arrayProperty.getItems() == null) {
				return new ArrayList<>(Collections.singleton(1));
			} else {
				return new ArrayList<>(Collections.singletonList(createValueForProperty("", arrayProperty.getItems(), definitions)));
			}
		}
		if (property instanceof AbstractNumericProperty) {
			AbstractNumericProperty numeric = (AbstractNumericProperty) property;
			BigDecimal numericPropertyValue = null;
			if (numeric.getMinimum() != null) {
				if (numeric.getExclusiveMinimum()) {
					numericPropertyValue = numeric.getMinimum().add(new BigDecimal(1));
				} else {
					numericPropertyValue = numeric.getMinimum();
				}
			}
			if (numeric.getMaximum() != null) {
				if (numeric.getExclusiveMaximum() != null) {
					numericPropertyValue = numeric.getMaximum().subtract(new BigDecimal(1));
				} else {
					numericPropertyValue = numeric.getMaximum();
				}
			}
			if (numeric instanceof DoubleProperty || numeric instanceof FloatProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.doubleValue();
				} else {
					return 1.1d;
				}
			}
			if (numeric instanceof LongProperty || numeric instanceof DecimalProperty || numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.longValue();
				} else {
					return 1;
					//TODO return Pattern.compile("[0-9]+");
				}
			}
			return 1;
			//TODO return Pattern.compile("[0-9]+");
		}
		if (property instanceof StringProperty) {
			StringProperty stringProperty = StringProperty.class.cast(property);
			if (stringProperty.getEnum() != null) {
				return stringProperty.getEnum().get(0);
			}
		}
		return key;
		//TODO return new MatchingTypeValue(MatchingType.REGEX, ".+");
	}

	static Map<String, Object> getJsonForPropertiesConstruct(String reference, Map<String, Model> definitions) {
		String referenceName = reference.substring(reference.lastIndexOf("/") + 1);
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> createValueForProperty(entry.getKey(), entry.getValue(), definitions)));
	}

	private static Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value;
		}
		if (value instanceof Double && (property.getFormat().equals("int32") || property.getFormat().equals("int64"))) {
			return Double.class.cast(value).intValue();
		}
		return value;
	}

	private static Object getDefaultValue(Property property) {
		if (property instanceof DoubleProperty) {
			return DoubleProperty.class.cast(property).getDefault();
		}
		if (property instanceof FloatProperty) {
			return FloatProperty.class.cast(property).getDefault();
		}
		if (property instanceof LongProperty) {
			return LongProperty.class.cast(property).getDefault();
		}
		if (property instanceof IntegerProperty) {
			return IntegerProperty.class.cast(property).getDefault();
		}
		if (property instanceof BooleanProperty) {
			return BooleanProperty.class.cast(property).getDefault();
		}
		if (property instanceof StringProperty) {
			return StringProperty.class.cast(property).getDefault();
		}
		return null;
	}
}

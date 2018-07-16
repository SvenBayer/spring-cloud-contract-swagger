package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import io.swagger.models.Model;
import io.swagger.models.properties.*;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_BOOLEAN;
import static de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_FLOAT;
import static de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_INT;
import static de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.*;

/**
 * @author Sven Bayer
 */
public final class ValuePropertyBuilder {

	private ValuePropertyBuilder() {
	}

	public static DslProperty createDslValueForProperty(String key, Property property, Map<String, Model> definitions) {
		Object value = createValueForProperty(key, property, definitions);
		//TODO avoid default values and set the pattern for the corresponding type
		return new DslProperty(String.valueOf(value));
	}

	static Object createValueForProperty(String key, Property property, Map<String, Model> definitions) {
		if (property.getExample() != null) {
			return postFormatNumericValue(property, property.getExample());
		}
		if (property.getVendorExtensions() != null && property.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()) != null) {
			return postFormatNumericValue(property, property.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.getField()));
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
				return new ArrayList<>(Collections.singleton(DEFAULT_INT));
			} else {
				return new ArrayList<>(Collections.singletonList(createValueForProperty(key, arrayProperty.getItems(), definitions)));
			}
		}
		if (property instanceof AbstractNumericProperty) {
			AbstractNumericProperty numeric = (AbstractNumericProperty) property;
			BigDecimal numericPropertyValue = null;
			if (numeric.getMinimum() != null) {
				if (numeric.getExclusiveMinimum()) {
					numericPropertyValue = numeric.getMinimum().add(new BigDecimal(DEFAULT_INT));
				} else {
					numericPropertyValue = numeric.getMinimum();
				}
			}
			if (numeric.getMaximum() != null) {
				if (numeric.getExclusiveMaximum() != null) {
					numericPropertyValue = numeric.getMaximum().subtract(new BigDecimal(DEFAULT_INT));
				} else {
					numericPropertyValue = numeric.getMaximum();
				}
			}
			if (numeric instanceof DoubleProperty || numeric instanceof FloatProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.doubleValue();
				} else {
					return DEFAULT_FLOAT;
				}
			}
			if (numeric instanceof LongProperty || numeric instanceof DecimalProperty
					|| numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
				if (numericPropertyValue != null) {
					return numericPropertyValue.longValue();
				} else {
					return DEFAULT_INT;
					//TODO return Pattern.compile("[0-9]+");
				}
			}
			return DEFAULT_INT;
			//TODO return Pattern.compile("[0-9]+");
		}
		if (property instanceof BooleanProperty) {
			return DEFAULT_BOOLEAN;
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
		String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> createValueForProperty(entry.getKey(), entry.getValue(), definitions)));
	}

	private static Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value;
		}
		if (value instanceof Double && (property.getFormat().equals(SwaggerFields.INT_32.getField()) || property.getFormat().equals(SwaggerFields.INT_64.getField()))) {
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

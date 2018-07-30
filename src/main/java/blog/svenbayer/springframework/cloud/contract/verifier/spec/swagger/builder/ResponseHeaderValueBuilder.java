package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.ReferenceResolverFactory;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.SwaggerReferenceResolver;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import io.swagger.models.Model;
import io.swagger.models.properties.*;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.*;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.INT_32;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.INT_64;

/**
 * Creates a value for a response header.
 *
 * @author Sven Bayer
 */
public final class ResponseHeaderValueBuilder {

	private static ReferenceResolverFactory refFactory = new ReferenceResolverFactory();

	/**
	 * Creates a dsl value for a response header property.
	 *
	 * @param key the key of the header
	 * @param property the response header property
	 * @param definitions the Swagger model definition
	 * @return the value for the given response header property
	 */
	public static DslProperty createDslResponseHeaderValue(String key, Property property, Map<String, Model> definitions) {
		Object value = createResponseHeaderValue(key, property, definitions);
		//TODO avoid default values
		// TODO Pattern does not appear for Property types
		return new DslProperty<>(value);
	}

	/**
	 * Creates a value for a response header property.
	 *
	 * @param key the key of the header
	 * @param property the response header property
	 * @param definitions the Swagger model definition
	 * @return the value for the given response header property
	 */
	public static Object createResponseHeaderValue(String key, Property property, Map<String, Model> definitions) {
		if (property.getExample() != null) {
			return postFormatNumericValue(property, property.getExample());
		}
		if (property.getVendorExtensions() != null && property.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.field()) != null) {
			return postFormatNumericValue(property, property.getVendorExtensions().get(SwaggerFields.X_EXAMPLE.field()));
		}
		Object defaultValue = getDefaultValue(property);
		if (defaultValue != null) {
			return defaultValue;
		}
		if (property instanceof RefProperty) {
			RefProperty refProperty = RefProperty.class.cast(property);
			String ref = refProperty.get$ref();
			SwaggerReferenceResolver resolver = refFactory.getReferenceResolver(refProperty.get$ref(), refProperty.getVendorExtensions());
			return resolver.resolveReference(definitions);
		}
		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = ArrayProperty.class.cast(property);
			if (arrayProperty.getItems() == null) {
				return new ArrayList<>(Collections.singleton(DEFAULT_INT));
			} else {
				return new ArrayList<>(Collections.singletonList(createResponseHeaderValue(key, arrayProperty.getItems(), definitions)));
			}
		}
		if (property instanceof AbstractNumericProperty) {
			return createDefaultNumericValue((AbstractNumericProperty) property);
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

	/**
	 * Creates a default numeric value for the given property
	 *
	 * @param property the numeric property
	 * @return the default value
	 */
	private static Object createDefaultNumericValue(AbstractNumericProperty property) {
		AbstractNumericProperty numeric = property;
		BigDecimal numericPropertyValue = null;
		if (numeric.getMinimum() != null) {
			if (numeric.getExclusiveMinimum() != null && numeric.getExclusiveMinimum()) {
				numericPropertyValue = numeric.getMinimum().add(new BigDecimal(DEFAULT_INT));
			} else {
				numericPropertyValue = numeric.getMinimum();
			}
		}
		if (numeric.getMaximum() != null) {
			if (numeric.getExclusiveMaximum() != null && numeric.getExclusiveMaximum()) {
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
				if (numeric instanceof LongProperty) {
					return numericPropertyValue.longValue();
				} else if (numeric instanceof DecimalProperty) {
					return numericPropertyValue;
				} else if (numeric instanceof IntegerProperty || numeric instanceof BaseIntegerProperty) {
					return numericPropertyValue.intValue();
				} else {
					return DEFAULT_INT;
				}
			} else {
				return DEFAULT_INT;
				//TODO return Pattern.compile("[0-9]+");
			}
		}
		return DEFAULT_INT;
		//TODO return Pattern.compile("[0-9]+");
	}

	/**
	 * Formats a numeric property correctly if its value is a double but its format is an int32 or int64.
	 *
	 * @param property the property
	 * @param value the value that could be a double
	 * @return the formatted property
	 */
	private static Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value;
		}
		if (value instanceof Double && (property.getFormat().equals(INT_32.type()) || property.getFormat().equals(INT_64.type()))) {
			return Double.class.cast(value).intValue();
		}
		return value;
	}

	/**
	 * Returns the property as typed property instance.
	 *
	 * @param property the property
	 * @return the specified typed property or null if not matching subclass is found
	 */
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

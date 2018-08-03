package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.ReferenceResolverFactory;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.SwaggerReferenceResolver;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import io.swagger.models.Model;
import io.swagger.models.properties.*;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_BOOLEAN;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_INT;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFormats.INT_32;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFormats.INT_64;

/**
 * Creates a value for a response header.
 *
 * @author Sven Bayer
 */
public final class ResponseHeaderValueBuilder {

	private ReferenceResolverFactory refFactory = new ReferenceResolverFactory();
	private NumericPropertyValueBuilder numericPropertyValueBuilder = new NumericPropertyValueBuilder();

	/**
	 * Creates a dsl value for a response header property.
	 *
	 * Note: Pattern does not appear for Property types
	 *
	 * @param key the key of the header
	 * @param property the response header property
	 * @param definitions the Swagger model definition
	 * @return the value for the given response header property
	 */
	public DslProperty createDslResponseHeaderValue(String key, Property property, Map<String, Model> definitions) {
		Object value = createResponseHeaderValue(key, property, definitions);
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
	public Object createResponseHeaderValue(String key, Property property, Map<String, Model> definitions) {
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
			RefProperty refProperty = (RefProperty) property;
			String ref = refProperty.get$ref();
			SwaggerReferenceResolver resolver = this.refFactory.getReferenceResolver(ref, refProperty.getVendorExtensions());
			return resolver.resolveReference(definitions);
		}
		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = (ArrayProperty) property;
			if (arrayProperty.getItems() == null) {
				return new ArrayList<>(Collections.singleton(DEFAULT_INT));
			} else {
				return new ArrayList<>(Collections.singletonList(createResponseHeaderValue(key, arrayProperty.getItems(), definitions)));
			}
		}
		if (property instanceof AbstractNumericProperty) {
			return this.numericPropertyValueBuilder.createDefaultNumericValue((AbstractNumericProperty) property);
		}
		if (property instanceof BooleanProperty) {
			return DEFAULT_BOOLEAN;
		}
		if (property instanceof StringProperty) {
			StringProperty stringProperty = (StringProperty) property;
			if (stringProperty.getEnum() != null) {
				return stringProperty.getEnum().get(0);
			}
		}
		return key;
	}

	/**
	 * Formats a numeric property correctly if its value is a double but its format is an int32 or int64.
	 *
	 * @param property the property
	 * @param value the value that could be a double
	 * @return the formatted property
	 */
	private Object postFormatNumericValue(Property property, Object value) {
		if (property.getFormat() == null) {
			return value;
		}
		if (value instanceof Double && (property.getFormat().equals(INT_32.format()) || property.getFormat().equals(INT_64.format()))) {
			return ((Double) value).intValue();
		}
		return value;
	}

	/**
	 * Returns the property as typed property instance.
	 *
	 * @param property the property
	 * @return the specified typed property or null if not matching subclass is found
	 */
	Object getDefaultValue(Property property) {
		if (property instanceof DoubleProperty) {
			return ((DoubleProperty) property).getDefault();
		}
		if (property instanceof FloatProperty) {
			return ((FloatProperty) property).getDefault();
		}
		if (property instanceof LongProperty) {
			return ((LongProperty) property).getDefault();
		}
		if (property instanceof IntegerProperty) {
			return ((IntegerProperty) property).getDefault();
		}
		if (property instanceof BooleanProperty) {
			return ((BooleanProperty) property).getDefault();
		}
		if (property instanceof StringProperty) {
			return ((StringProperty) property).getDefault();
		}
		return null;
	}
}

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues;
import io.swagger.models.parameters.AbstractSerializableParameter;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_EXAMPLE;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.*;

/**
 * Creates values for query and header parameters.
 *
 * @author Sven Bayer
 */
public final class DslValueBuilder {

	private DslValueBuilder() {
	}

	/**
	 * Creates a dsl value for a query or header parameter.
	 *
	 * @param param the query or header parameter
	 * @return the dsl value
	 */
	public static DslProperty<Object> createDslValueForParameter(AbstractSerializableParameter param) {
		if (param.getVendorExtensions() != null) {
			Object ignore = param.getVendorExtensions().get("x-ignore");
			if (ignore != null && Boolean.valueOf(ignore.toString())) {
				if (param.getRequired()) {
					throw new SwaggerContractConverterException("Set the parameter '" + param.getName() + "' to required: false to use x-ignore: true");
				}
				return null;
			}
		}
		Object value = createServerValueForParameter(param);
		if (value == null) {
			value = createDefaultValueForType(param);
			if (param.pattern == null) {
				Pattern pattern = createPatternForParameter(param);
				return new DslProperty<>(pattern, value);
			}
		}
		DslProperty<Object> dslProperty;
		// TODO we need to check if the pattern matches
		if (param.pattern != null) {
			dslProperty = new DslProperty<>(Pattern.compile(param.pattern), value);
		} else {
			dslProperty = new DslProperty<>(value);
		}
		return dslProperty;
	}

	/**
	 * Tries to extract the example value from a parameter.
	 *
	 * @param param the parameter
	 * @return the example value
	 */
	private static Object createServerValueForParameter(AbstractSerializableParameter param) {
		if (param.getExample() != null) {
			return param.getExample();
		}
		if (param.getVendorExtensions() != null && param.getVendorExtensions().get(X_EXAMPLE.field()) != null) {
			return param.getVendorExtensions().get(X_EXAMPLE.field());
		}
		if (param.getDefaultValue() != null) {
			return param.getDefaultValue();
		}
		if (param.getEnum() != null && param.getEnum().get(0) != null) {
			return param.getEnum().get(0);
		}
		return null;
	}

	/**
	 * Creates a pattern for a given parameter.
	 *
	 * @param param the parameter
	 * @return the pattern
	 */
	private static Pattern createPatternForParameter(AbstractSerializableParameter param) {
		String regex = createRegexForDefaultValue(param);
		return Pattern.compile(regex);
	}

	/**
	 * Creates a regex for a given parameter.
	 *
	 * @param param the parameter
	 * @return the regex
	 */
	private static String createRegexForDefaultValue(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if (STRING.type().equals(type)) {
			return ".+";
		}
		if ((NUMBER.type().equals(type)) && (DOUBLE.type().equals(format) || FLOAT.type().equals(format))) {
			return "[0-9]+\\.[0-9]+";
		}
		if (NUMBER.type().equals(type)) {
			return "[0-9]+";
		}
		if (BOOLEAN.type().equals(type)) {
			return "(true|false)";
		}
		return ".+";
	}

	/**
	 * Creates a default value for a given parameter.
	 *
	 * @param param the parameter
	 * @return the default value
	 */
	private static Object createDefaultValueForType(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if (STRING.type().equals(type)) {
			if (param.getName() != null && !param.getName().isEmpty()) {
				return param.getName();
			} else {
				return STRING.type();
			}
		}
		if ((NUMBER.type().equals(type)) && (DOUBLE.type().equals(format) || FLOAT.type().equals(format))) {
			return DefaultValues.DEFAULT_FLOAT;
		}
		if (NUMBER.type().equals(type)) {
			return DefaultValues.DEFAULT_INT;
		}
		if (BOOLEAN.type().equals(type)) {
			return DefaultValues.DEFAULT_BOOLEAN;
		}
		return DefaultValues.DEFAULT_INT;
	}
}

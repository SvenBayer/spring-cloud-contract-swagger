package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.builder;

import de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues;
import io.swagger.models.parameters.AbstractSerializableParameter;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.util.regex.Pattern;

import static de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.*;

/**
 * @author Sven Bayer
 */
public final class DslValueBuilder {

	private DslValueBuilder() {
	}

	public static DslProperty<Object> createDslValueForParameter(AbstractSerializableParameter param) {
		Object value = createServerValueForParameter(param);
		if (value == null) {
			value = createDefaultValueForType(param);
			if (param.pattern == null) {
				Pattern pattern = createPatternForDefaultValue(param);
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

	private static Object createServerValueForParameter(AbstractSerializableParameter param) {
		if (param.getExample() != null) {
			return param.getExample();
		}
		if (param.getVendorExtensions() != null && param.getVendorExtensions().get(X_EXAMPLE.getField()) != null) {
			return param.getVendorExtensions().get(X_EXAMPLE.getField());
		}
		if (param.getDefaultValue() != null) {
			return param.getDefaultValue();
		}
		if (param.getEnum() != null && param.getEnum().get(0) != null) {
			return param.getEnum().get(0);
		}
		return null;
	}

	private static Pattern createPatternForDefaultValue(AbstractSerializableParameter param) {
		String regex = createRegexForDefaultValue(param);
		return Pattern.compile(regex);
	}

	private static String createRegexForDefaultValue(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if (STRING.getField().equals(type)) {
			return ".+";
		}
		if ((NUMBER.getField().equals(type)) && (DOUBLE.getField().equals(format) || FLOAT.getField().equals(format))) {
			return "[0-9]+\\.[0-9]+";
		}
		if (NUMBER.getField().equals(type)) {
			return "[0-9]+";
		}
		if (BOOLEAN.getField().equals(type)) {
			return "(true|false)";
		}
		return ".+";
	}

	private static Object createDefaultValueForType(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if (STRING.getField().equals(type)) {
			if (param.getName() != null && !param.getName().isEmpty()) {
				return param.getName();
			} else {
				return STRING.getField();
			}
		}
		if ((NUMBER.getField().equals(type)) && (DOUBLE.getField().equals(format) || FLOAT.getField().equals(format))) {
			return DefaultValues.DEFAULT_FLOAT;
		}
		if (NUMBER.getField().equals(type)) {
			return DefaultValues.DEFAULT_INT;
		}
		if (BOOLEAN.getField().equals(type)) {
			return true;
		}
		return DefaultValues.DEFAULT_INT;
	}
}

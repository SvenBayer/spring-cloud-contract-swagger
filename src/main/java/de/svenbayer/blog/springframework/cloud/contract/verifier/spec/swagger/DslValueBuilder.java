package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger;

import io.swagger.models.parameters.AbstractSerializableParameter;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.util.regex.Pattern;

public class DslValueBuilder {

	static DslProperty createDslValueForParameter(AbstractSerializableParameter param) {
		Object value = createServerValueForParameter(param);
		if (value == null) {
			value = createDefaultValueForType(param);
			if (param.pattern == null) {
				Pattern pattern = createPatternForDefaultValue(param);
				return new DslProperty(pattern, value);
			}
		}
		DslProperty dslProperty;
		// TODO we need to check if the pattern matches
		if (param.pattern != null) {
			dslProperty = new DslProperty(Pattern.compile(param.pattern), value);
		} else {
			dslProperty = new DslProperty(value);
		}
		return dslProperty;
	}

	private static Object createServerValueForParameter(AbstractSerializableParameter param) {
		if (param.getExample() != null) {
			return param.getExample();
		}
		if (param.getVendorExtensions() != null && param.getVendorExtensions().get("x-example") != null) {
			return param.getVendorExtensions().get("x-example");
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

		if ("string".equals(type)) {
			return ".+";
		}
		if (("number".equals(type)) && ("double".equals(format) || "float".equals(format))) {
			return "[0-9]+\\.[0-9]+";
		}
		if ("number".equals(type)) {
			return "[0-9]+";
		}
		if ("boolean".equals(type)) {
			return "(true|false)";
		}
		return ".+";
	}

	private static Object createDefaultValueForType(AbstractSerializableParameter param) {
		String type = param.getType();
		String format = param.getFormat();

		if ("string".equals(type)) {
			if (param.getName() != null && !param.getName().isEmpty()) {
				return param.getName();
			} else {
				return "string";
			}
		}
		if (("number".equals(type)) && ("double".equals(format) || "float".equals(format))) {
			return 1.1;
		}
		if ("number".equals(type)) {
			return 1;
		}
		if ("boolean".equals(type)) {
			return true;
		}
		return 1;
	}
}

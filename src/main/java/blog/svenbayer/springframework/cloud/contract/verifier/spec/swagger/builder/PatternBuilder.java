package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.*;

/**
 * Builds pattern for matching in contract tests.
 *
 * @author Sven Bayer
 */
class PatternBuilder {

	private PatternBuilder() {
	}

	/**
	 * Creates a pattern for a given parameter.
	 *
	 * @param type the primitive type
	 * @param format the parameter
	 * @return the pattern
	 */
	static Pattern createPatternForParameter(String type, String format) {
		String regex = createRegexForDefaultValue(type, format);
		return Pattern.compile(regex);
	}

	/**
	 * Creates a regex for a given parameter.
	 *
	 * @param type the primitive type
	 * @param format the parameter
	 * @return the regex
	 */
	private static String createRegexForDefaultValue(String type, String format) {
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
}

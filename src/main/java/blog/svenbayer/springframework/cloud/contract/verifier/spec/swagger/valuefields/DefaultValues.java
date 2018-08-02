package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.*;

/**
 * Default values that we set in a Spring Cloud Contract if no example value is set.
 *
 * @author Sven Bayer
 */
public final class DefaultValues {

	private DefaultValues() {
	}

	/**
	 * The default value for floats and double.
	 */
	public static final double DEFAULT_FLOAT = 1.1d;

	/**
	 * The default value for integers and anything undefined.
	 */
	public static final int DEFAULT_INT = 1;

	/**
	 * The default value for booleans.
	 */
	public static final boolean DEFAULT_BOOLEAN = true;

	/**
	 * Creates a default value for a given parameter.
	 *
	 * @param type the primitive type
	 * @param format the specific format
	 * @param name the name of the parameter
	 * @return the default value
	 */
	public static Object createDefaultValueForType(String type, String format, String name) {
		if (STRING.type().equals(type)) {
			if (name != null && !name.isEmpty()) {
				return name;
			} else {
				return STRING.type();
			}
		}
		if ((NUMBER.type().equals(type)) && (DOUBLE.type().equals(format) || FLOAT.type().equals(format))) {
			return DEFAULT_FLOAT;
		}
		if (NUMBER.type().equals(type)) {
			return DEFAULT_INT;
		}
		if (BOOLEAN.type().equals(type)) {
			return DEFAULT_BOOLEAN;
		}
		return DEFAULT_INT;
	}
}

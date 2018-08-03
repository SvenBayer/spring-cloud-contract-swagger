package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

/**
 * Represents Swagger formats, which are more detailed than types.
 *
 * @author Sven Bayer
 */
public enum SwaggerFormats {
	DOUBLE("double"),
	FLOAT("float"),
	INT_32("int32"),
	INT_64("int64"),
	BYTE("byte"),
	BINARY("binary"),
	DATE("date"),
	DATE_TIME("date-time"),
	PASSWORD("password");

	private String format;

	SwaggerFormats(String swaggerFormat) {
		this.format = swaggerFormat;
	}

	public String format() {
		return this.format;
	}
}

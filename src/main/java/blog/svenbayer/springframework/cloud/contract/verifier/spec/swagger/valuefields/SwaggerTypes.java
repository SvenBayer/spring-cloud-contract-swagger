package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

/**
 * Types of a Swagger document.
 *
 * @author Sven Bayer
 */
public enum SwaggerTypes {
	STRING("string"),
	NUMBER("number"),
	BOOLEAN("boolean"),
	DOUBLE("double"),
	FLOAT("float"),
	INT_32("int32"),
	INT_64("int64");

	private String swaggerType;

	SwaggerTypes(String type) {
		this.swaggerType = type;
	}

	/**
	 * Returns the Swagger type as string.
	 *
	 * @return the Swagger type
	 */
	public String type() {
		return this.swaggerType;
	}
}

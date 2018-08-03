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
	INTEGER("integer");

	private String type;

	SwaggerTypes(String swaggerType) {
		this.type = swaggerType;
	}

	/**
	 * Returns the Swagger type as string.
	 *
	 * @return the Swagger type
	 */
	public String type() {
		return this.type;
	}
}

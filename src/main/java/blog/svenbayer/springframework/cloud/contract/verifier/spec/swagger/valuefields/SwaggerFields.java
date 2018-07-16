package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

/**
 * @author Sven Bayer
 */
public enum SwaggerFields {
	X_EXAMPLE("x-example"),
	STRING("string"),
	NUMBER("number"),
	BOOLEAN("boolean"),
	DOUBLE("double"),
	FLOAT("float"),
	X_IGNORE("x-ignore"),
	INT_32("int32"),
	INT_64("int64");

	private String swaggerField;

	SwaggerFields(String field) {
		this.swaggerField = field;
	}

	public String getField() {
		return this.swaggerField;
	}
}

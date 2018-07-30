package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

/**
 * Vendor fields of a Swagger document.
 *
 * @author Sven Bayer
 */
public enum SwaggerFields {
	X_EXAMPLE("x-example"),
	X_REF("x-ref"),
	X_IGNORE("x-ignore");

	private String swaggerField;

	SwaggerFields(String field) {
		this.swaggerField = field;
	}

	public String field() {
		return this.swaggerField;
	}
}

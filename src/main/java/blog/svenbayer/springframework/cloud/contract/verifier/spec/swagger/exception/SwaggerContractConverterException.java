package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception;

/**
 * Exception for Swagger Contract Converter related errors.
 *
 * @author Sven Bayer
 */
public class SwaggerContractConverterException extends RuntimeException {

	public SwaggerContractConverterException(String message) {
		super(message);
	}

	public SwaggerContractConverterException(String message, Throwable cause) {
		super(message, cause);
	}
}
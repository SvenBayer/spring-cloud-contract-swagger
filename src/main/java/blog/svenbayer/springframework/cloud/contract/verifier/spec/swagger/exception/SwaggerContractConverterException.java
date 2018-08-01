package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception;

/**
 * Exception for Swagger Contract Converter related errors.
 *
 * @author Sven Bayer
 */
public class SwaggerContractConverterException extends RuntimeException {

	/**
	 * Swagger Contract Converter Exception with message.
	 *
	 * @param message the error message
	 */
	public SwaggerContractConverterException(String message) {
		super(message);
	}

	/**
	 * Swagger Contract Converter Exception with message and cause.
	 *
	 * @param message the error message
	 * @param cause the cause
	 */
	public SwaggerContractConverterException(String message, Throwable cause) {
		super(message, cause);
	}
}
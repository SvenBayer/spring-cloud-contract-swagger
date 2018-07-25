package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

/**
 * Default values that we set in a Spring Cloud Contract if no example value is set.
 *
 * @author Sven Bayer
 */
public final class DefaultValues {

	private DefaultValues() {
	}

	public static final double DEFAULT_FLOAT = 1.1d;
	public static final int DEFAULT_INT = 1;
	public static final boolean DEFAULT_BOOLEAN = true;
}

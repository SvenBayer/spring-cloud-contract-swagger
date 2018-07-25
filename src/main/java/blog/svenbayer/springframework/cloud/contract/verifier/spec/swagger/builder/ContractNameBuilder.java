package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.HttpMethod;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds a contract name for a given path and http method.
 *
 * @author Sven Bayer
 */
public class ContractNameBuilder {

	private static final String PATH_SEP = "_";

	/**
	 * Extract the path without the leading slash and without the last closing curly-brace
	 */
	private static final String PATH_EXTRACT = "([^\\/].*[^\\}])";

	/**
	 * Replace all curly-braces and slashes.
	 */
	private static final String PATH_CLEANUP = "(\\}\\/\\{|\\/\\{|\\}\\/|\\}|\\/)";

	private ContractNameBuilder() {
	}

	/**
	 * Creates a contract name for a given path and http method.
	 *
	 * @param priority the order of the method
	 * @param pathLink the path of the endpoint
	 * @param httpMethod the operation (GET, POST, PUT, DELETE)
	 * @return the formatted contract name
	 */
	public static String createContractName(AtomicInteger priority, String pathLink, HttpMethod httpMethod) {
		Matcher pathMatcher = Pattern.compile(PATH_EXTRACT).matcher(pathLink);
		if (!pathMatcher.find()) {
			throw new SwaggerContractConverterException("Could not extract path of method from Swagger file: " + pathLink);
		}
		String extractedPath = pathMatcher.group(1);
		String cleanedUpPathLink = extractedPath.replaceAll(PATH_CLEANUP, PATH_SEP);
		return priority + PATH_SEP + cleanedUpPathLink + PATH_SEP + httpMethod.name();
	}
}

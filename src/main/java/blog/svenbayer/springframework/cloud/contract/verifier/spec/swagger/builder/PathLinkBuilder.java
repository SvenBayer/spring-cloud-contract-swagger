package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.HttpMethod;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathLinkBuilder {

	private static final String PATH_SEP = "_";
	private static final String PATH_EXTRACT = "([^\\/].*[^\\}])";
	private static final String PATH_CLEANUP = "(\\}\\/\\{|\\/\\{|\\}\\/|\\}|\\/)";

	private PathLinkBuilder() {
	}

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

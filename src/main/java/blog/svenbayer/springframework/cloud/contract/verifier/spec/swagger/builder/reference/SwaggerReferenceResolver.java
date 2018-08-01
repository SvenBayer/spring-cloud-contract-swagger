package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import io.swagger.models.Model;

import java.util.Map;

/**
 * Resolves a swagger reference.
 *
 * @author Sven Bayer
 */
public interface SwaggerReferenceResolver {

	/**
	 * Resolves a swagger reference for the given Swagger definitions.
	 *
	 * @param definitions the Swagger definitions
	 * @return the json
	 */
	String resolveReference(Map<String, Model> definitions);
}

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import io.swagger.models.Model;

import java.util.Map;

/**
 * @author Sven Bayer
 */
public interface SwaggerReferenceResolver {

	String resolveReference(Map<String, Model> definitions);
}

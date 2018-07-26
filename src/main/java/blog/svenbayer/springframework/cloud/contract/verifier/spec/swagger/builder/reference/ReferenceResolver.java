package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import io.swagger.models.Model;

import java.util.Map;

public interface ReferenceResolver {

	String resolveReference(String reference, Map<String, Model> definitions);
}

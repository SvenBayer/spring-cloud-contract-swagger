package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;

import java.util.HashMap;
import java.util.Map;

public class ReferenceResolverFactory {

	private static final Map<Character, ReferenceResolver> RESOLVER = new HashMap<>();

	static {
		RESOLVER.put('#', new SwaggerDefinitionsRefResolver());
	}

	public ReferenceResolver getReferenceResolver(String reference) {
		if (reference == null || reference.isEmpty()) {
			throw new SwaggerContractConverterException("Swagger reference must not be null or empty!");
		}
		char refStart = reference.charAt(0);
		ReferenceResolver resolver = RESOLVER.get(refStart);
		if (resolver == null) {
			throw new SwaggerContractConverterException("Could not find resolver for given reference '" + reference + "'");
		}
		return resolver;
	}
}

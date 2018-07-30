package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;

import java.util.Map;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_REF;

/**
 * @author Sven Bayer
 */
public class ReferenceResolverFactory {

	public SwaggerReferenceResolver getReferenceResolver(String reference, Map<String, Object> vendorExtensions) {
		if (vendorExtensions != null && vendorExtensions.get(X_REF.field()) != null) {
			String refFile = String.valueOf(vendorExtensions.get(X_REF.field()));
			String cleanedUpRefFile = refFile.replaceAll("\\.\\/", "");
			return new JsonFileResolverSwagger(cleanedUpRefFile);
		} else {
			if (reference == null || reference.isEmpty()) {
				throw new SwaggerContractConverterException("Swagger reference must not be null or empty!");
			}
			return new SwaggerDefinitionsRefResolverSwagger(reference);
		}

	}
}

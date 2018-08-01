package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;

import java.util.Map;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_REF;

/**
 * A factory that provides the corresponding resolver for Swagger references.
 *
 * @author Sven Bayer
 */
public class ReferenceResolverFactory {

	/**
	 * Returns the reference resolver for the given Swagger reference and x- attributes.
	 *
	 * @param reference the Swagger reference
	 * @param vendorExtensions the x- attributes
	 * @return the reference resolver
	 */
	public SwaggerReferenceResolver getReferenceResolver(String reference, Map<String, Object> vendorExtensions) {
		if (vendorExtensions != null && vendorExtensions.get(X_REF.field()) != null) {
			String refFile = String.valueOf(vendorExtensions.get(X_REF.field()));
			String cleanedUpRefFile = refFile.replaceAll("\\.\\/", "");
			return new JsonFileResolverSwagger(cleanedUpRefFile, reference);
		} else {
			if (reference == null || reference.isEmpty()) {
				throw new SwaggerContractConverterException("Swagger reference must not be null or empty!");
			}
			return new SwaggerDefinitionsRefResolverSwagger(reference);
		}

	}
}

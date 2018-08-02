package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.ReferenceResolverFactory;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.SwaggerReferenceResolver;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.Model;
import io.swagger.models.Response;

import java.util.Map;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields.X_EXAMPLE;

/**
 * Creates the value for a response body.
 *
 * @author Sven Bayer
 */
public final class ResponseBodyBuilder {

	private ReferenceResolverFactory refFactory = new ReferenceResolverFactory();

	/**
	 * Creates the value for a response body and given Swagger model definitions.
	 *
	 * @param response the Swagger response
	 * @param definitions the Swagger model definitions
	 * @return the value for the response body
	 */
	public String createValueForResponseBody(Response response, Map<String, Model> definitions) {
		if (response.getExamples() != null && !response.getExamples().values().isEmpty() && response.getExamples().values().toArray()[0] != null) {
			return String.valueOf(response.getExamples().values().toArray()[0]);
		} else if (response.getVendorExtensions() != null && response.getVendorExtensions().get(X_EXAMPLE.field()) != null) {
			return String.valueOf(response.getVendorExtensions().get(X_EXAMPLE.field()));
		} else if (response.getResponseSchema() != null) {
			if (response.getResponseSchema().getExample() != null) {
				return String.valueOf(response.getResponseSchema().getExample());
			} else if (response.getResponseSchema().getVendorExtensions() != null && response.getResponseSchema().getVendorExtensions().get(X_EXAMPLE.field()) != null) {
				return String.valueOf(response.getResponseSchema().getVendorExtensions().get(X_EXAMPLE.field()));
			} else if (response.getResponseSchema().getReference() != null) {
				String reference = response.getResponseSchema().getReference();
				SwaggerReferenceResolver resolver = this.refFactory.getReferenceResolver(reference, response.getVendorExtensions());
				return resolver.resolveReference(definitions);
			} else {
				throw new SwaggerContractConverterException("Could not parse body for response");
			}
		} else {
			throw new SwaggerContractConverterException("Could not parse body for response");
		}
	}
}

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SwaggerDefinitionsRefResolverSwaggerTest {

	@DisplayName("Resolve Swagger definitions for invalid json")
	@Test
	public void resolveDefinitionsForInvalidJson() {
		SwaggerDefinitionsRefResolverSwagger resolver = new SwaggerDefinitionsRefResolverSwagger("#invalid");
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.resolveReference(null);
		});
		assertEquals("Reference '#invalid' does not exist in definitions", exception.getMessage());

	}
}
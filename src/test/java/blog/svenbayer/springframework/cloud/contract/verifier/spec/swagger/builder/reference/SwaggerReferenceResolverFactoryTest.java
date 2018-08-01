package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
public class SwaggerReferenceResolverFactoryTest {

	private ReferenceResolverFactory factory;

	@Before
	public void init() {
		factory = new ReferenceResolverFactory();
	}

	@DisplayName("Fails to receive SwaggerReferenceResolver for null")
	@Test
	public void failsforNull() {
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver(null, null);
		});
		assertEquals("Swagger reference must not be null or empty!", exception.getMessage());
	}

	@DisplayName("Fails to receive SwaggerReferenceResolver for empty string")
	@Test
	public void failsForEmptyString() {
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver("", null);
		});
		assertEquals("Swagger reference must not be null or empty!", exception.getMessage());
	}
}
package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
class SwaggerReferenceResolverFactoryTest {

	private ReferenceResolverFactory factory;

	@BeforeEach
	void init() {
		factory = new ReferenceResolverFactory();
	}

	@DisplayName("Fails to receive SwaggerReferenceResolver for null")
	@Test
	void failsforNull() {
		assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver(null, null);
		});
	}

	@DisplayName("Fails to receive SwaggerReferenceResolver for empty string")
	@Test
	void failsForEmptyString() {
		assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver("", null);
		});
	}
}
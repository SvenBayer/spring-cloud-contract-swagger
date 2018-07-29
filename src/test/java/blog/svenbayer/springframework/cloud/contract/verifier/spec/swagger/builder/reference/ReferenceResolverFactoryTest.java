package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
class ReferenceResolverFactoryTest {

	private ReferenceResolverFactory factory;

	@BeforeEach
	void init() {
		factory = new ReferenceResolverFactory();
	}

	@DisplayName("Fails to receive ReferenceResolver for null")
	@Test
	void failsforNull() {
		assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver(null);
		});
	}

	@DisplayName("Fails to receive ReferenceResolver for empty string")
	@Test
	void failsForEmptyString() {
		assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver("");
		});
	}

	@DisplayName("Fails to receive ReferenceResolver for unknown resolver")
	@Test
	void failsForUnknownResolver() {
		assertThrows(SwaggerContractConverterException.class, () -> {
			factory.getReferenceResolver("&");
		});
	}
}
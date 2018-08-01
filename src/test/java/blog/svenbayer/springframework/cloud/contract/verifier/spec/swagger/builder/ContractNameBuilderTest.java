package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.HttpMethod;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
public class ContractNameBuilderTest {

	@DisplayName("Escapes path for Contract name with path parameter and slashes")
	@Test
	public void escapePathForContractName() {
		String pathName = "/find/planets/{solarSystem}/{system}";
		AtomicInteger priority = new AtomicInteger(1);
		HttpMethod post = HttpMethod.POST;
		String contractName = ContractNameBuilder.createContractName(priority, pathName, post);
		assertEquals("1_find_planets_solarSystem_system_POST", contractName);
	}

	@DisplayName("Expect Exception for empty Contract path")
	@Test
	public void expectExceptionForEmptyPath() {
		String pathName = "";
		AtomicInteger priority = new AtomicInteger(1);
		HttpMethod post = HttpMethod.POST;
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			ContractNameBuilder.createContractName(priority, pathName, post);
		});
		assertEquals("Could not extract path of method from Swagger file: ", exception.getMessage());
	}
}
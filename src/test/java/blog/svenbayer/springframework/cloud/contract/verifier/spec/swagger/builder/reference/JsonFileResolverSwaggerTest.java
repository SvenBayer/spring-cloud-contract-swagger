package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.SwaggerFileFolder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestFileResourceLoader;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
class JsonFileResolverSwaggerTest {

	@DisplayName("Should throw exception for not existing path")
	@Test
	public void notExistingPath() {
		SwaggerFileFolder.setPathToSwaggerFile(Paths.get(""));
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("doesNotExist", "doesNotMatterForThisTest");
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.resolveReference(new HashMap<>());
		});
		assertEquals(exception.getMessage(), "Swagger file must only referenceFile files that exist. Could not find file '/doesNotExist'");
	}

	@DisplayName("Should throw exception for directory")
	@Test
	public void directory() {
		SwaggerFileFolder.setPathToSwaggerFile(Paths.get(""));
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("target", "doesNotMatterForThisTest");
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.resolveReference(new HashMap<>());
		});
		assertEquals(exception.getMessage(), "Swagger file must only referenceFile files that exist. Could not find file '/target'");
	}

	@DisplayName("Should ignore comparison if no Swagger definitions available")
	@Test
	void ignoreComparison() throws IOException {
		String expectedJson = TestFileResourceLoader.getResourceAsString("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");

		SwaggerFileFolder.setPathToSwaggerFile(TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/").toPath());
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("CoffeeRocket.json", "doesNotMatterForThisTest");
		String actualJson = resolver.resolveReference(null);

		assertEquals(expectedJson, actualJson);
	}
}
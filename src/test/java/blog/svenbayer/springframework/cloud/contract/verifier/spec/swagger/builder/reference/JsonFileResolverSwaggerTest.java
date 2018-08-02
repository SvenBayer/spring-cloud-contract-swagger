package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.SwaggerFileFolder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestFileResourceLoader;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.Property;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
public class JsonFileResolverSwaggerTest {

	@DisplayName("Should throw exception for not existing path")
	@Test
	public void notExistingPath() {
		SwaggerFileFolder.instance().setPathToSwaggerFile(Paths.get(""));
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("doesNotExist", "doesNotMatterForThisTest");
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.resolveReference(new HashMap<>());
		});
		assertEquals("Swagger file must only referenceFile files that exist. Could not find file '/doesNotExist'", exception.getMessage());
	}

	@DisplayName("Should throw exception for directory")
	@Test
	public void directory() {
		SwaggerFileFolder.instance().setPathToSwaggerFile(Paths.get(""));
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("target", "doesNotMatterForThisTest");
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.resolveReference(new HashMap<>());
		});
		assertEquals("Swagger file must only referenceFile files that exist. Could not find file '/target'", exception.getMessage());
	}

	@DisplayName("Should ignore comparison if no Swagger definitions available")
	@Test
	public void ignoreComparison() throws IOException {
		String expectedJson = TestFileResourceLoader.getResourceAsString("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");

		SwaggerFileFolder.instance().setPathToSwaggerFile(TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/").toPath());
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("CoffeeRocket.json", "doesNotMatterForThisTest");
		String actualJson = resolver.resolveReference(null);

		assertEquals(expectedJson, actualJson);
	}

	@DisplayName("Should throw exception for not equal jsons")
	@Test
	public void notEqualJsons() {
		SwaggerFileFolder.instance().setPathToSwaggerFile(TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withJsonMoreFields/").toPath());
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("CoffeeRocket.json", "#/definitions/CoffeeRocket");

		HashMap<String, Model> definitions = new HashMap<>();
		ModelImpl model = new ModelImpl();
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("key1", new IntegerProperty());

		model.setProperties(properties);
		definitions.put("CoffeeRocket", model);

		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.resolveReference(definitions);
		});
		assertThat(exception.getMessage(), startsWith("Swagger definitions and Json file should be equal but was not for:"));
	}

	@DisplayName("Ignore validations for null definitions")
	@Test
	public void nullDefinitions() {
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("CoffeeRocket.json", "#/definitions/CoffeeRocket");
		resolver.validateExternalJson("{}", null);
	}

	@DisplayName("Ignore validations for empty definitions")
	@Test
	public void emptyDefinitions() {
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("CoffeeRocket.json", "#/definitions/CoffeeRocket");
		resolver.validateExternalJson("{}", new HashMap<>());
	}


	@DisplayName("Should throw exception for not equal jsons validation")
	@Test
	public void notEqualJsonsValidation() {
		JsonFileResolverSwagger resolver = new JsonFileResolverSwagger("CoffeeRocket.json", "#/definitions/CoffeeRocket");
		HashMap<String, Model> definitions = new HashMap<>();
		ModelImpl model = new ModelImpl();
		HashMap<String, Property> properties = new HashMap<>();
		properties.put("key1", new IntegerProperty());

		model.setProperties(properties);
		definitions.put("CoffeeRocket", model);

		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			resolver.validateExternalJson("{}", definitions);
		});
		assertThat(exception.getMessage(), startsWith("Swagger definitions and Json file should be equal but was not for:"));
	}
}
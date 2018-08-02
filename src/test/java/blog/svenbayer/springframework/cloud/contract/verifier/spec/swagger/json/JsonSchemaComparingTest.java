package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestFileResourceLoader;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.SwaggerDefinitionsRefResolverSwagger;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sven Bayer
 */
public class JsonSchemaComparingTest {

	private JsonSchemaComparing jsonSchemaComparing;

	@Before
	public void init() {
		jsonSchemaComparing = new JsonSchemaComparing();
	}

	@DisplayName("Should be true for equal Jsons")
	@Test
	public void equalJsons() throws IOException {
		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		assertTrue(jsonSchemaComparing.isEquals(json, json), "Same json files should be equals!");
	}

	@DisplayName("Should be true for equal Jsons with different values")
	@Test
	public void equalJsonsDifferentValues() throws IOException {
		File expectedJsonFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withDifferentValues/CoffeeRocket1.json");
		String expectedJson = new String(Files.readAllBytes(expectedJsonFile.toPath()));

		File actualJsonFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withDifferentValues/CoffeeRocket2.json");
		String actualJson = new String(Files.readAllBytes(actualJsonFile.toPath()));

		assertTrue(jsonSchemaComparing.isEquals(expectedJson, actualJson), "Same json files with differnet values should be equals!");
	}

	@DisplayName("Should be false for different Json files")
	@Test
	public void differentJsonFiles() throws IOException {
		File expectedJsonFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withDifferentJsons/CoffeeRocket1.json");
		String expectedJson = new String(Files.readAllBytes(expectedJsonFile.toPath()));

		File actualJsonFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withDifferentJsons/CoffeeRocket2.json");
		String actualJson = new String(Files.readAllBytes(actualJsonFile.toPath()));

		assertFalse(jsonSchemaComparing.isEquals(expectedJson, actualJson), "Different Jsons should result in false!");
	}

	@DisplayName("Json file and Swagger definitions should be equal")
	@Test
	public void equalJsonFileAndSwaggerDefinitions() throws IOException {
		File swaggerFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/external_json_swagger.yml");
		Swagger swagger = new SwaggerParser().read(swaggerFile.getPath());
		Map<String, Model> definitions = swagger.getDefinitions();
		SwaggerDefinitionsRefResolverSwagger swaggerDefinitionsRefResolverSwagger = new SwaggerDefinitionsRefResolverSwagger("#/definitions/CoffeeRocket");
		String expectedJson = swaggerDefinitionsRefResolverSwagger.resolveReference(definitions);

		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String actualJson = new String(Files.readAllBytes(jsonFile.toPath()));

		assertTrue(jsonSchemaComparing.isEquals(expectedJson, actualJson), "Json from Swagger definitions and Json file should be equal!");
	}

	@DisplayName("Should be not equal for Json with more fields than Swagger definitions")
	@Test
	public void withJsonMoreFields() throws IOException {
		File swaggerFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withJsonMoreFields/external_json_swagger.yml");
		Swagger swagger = new SwaggerParser().read(swaggerFile.getPath());
		Map<String, Model> definitions = swagger.getDefinitions();
		SwaggerDefinitionsRefResolverSwagger swaggerDefinitionsRefResolverSwagger = new SwaggerDefinitionsRefResolverSwagger("#/definitions/CoffeeRocket");
		String expectedJson = swaggerDefinitionsRefResolverSwagger.resolveReference(definitions);

		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withJsonMoreFields/CoffeeRocket.json");
		String actualJson = new String(Files.readAllBytes(jsonFile.toPath()));

		assertFalse(jsonSchemaComparing.isEquals(expectedJson, actualJson), "Json file with more fields than Swagger definitions should be false!");
	}

	@DisplayName("Should be not equal for Swagger definitions with more fields than Json")
	@Test
	public void withSwaggerMoreFields() throws IOException {
		File swaggerFile = TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withSwaggerMoreFields/external_json_swagger.yml");
		Swagger swagger = new SwaggerParser().read(swaggerFile.getPath());
		Map<String, Model> definitions = swagger.getDefinitions();
		SwaggerDefinitionsRefResolverSwagger swaggerDefinitionsRefResolverSwagger = new SwaggerDefinitionsRefResolverSwagger("#/definitions/CoffeeRocket");
		String expectedJson = swaggerDefinitionsRefResolverSwagger.resolveReference(definitions);

		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withSwaggerMoreFields/CoffeeRocket.json");
		String actualJson = new String(Files.readAllBytes(jsonFile.toPath()));

		assertFalse(jsonSchemaComparing.isEquals(expectedJson, actualJson), "Swagger definitions with more fields than Json file should be false!");
	}

	@DisplayName("Should throw exception for expectedJson null")
	@Test
	public void expectedJsonNull() throws IOException {
		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			jsonSchemaComparing.isEquals(null, json);
		});
		assertEquals("JSON of Swagger definitions must not be null!", exception.getMessage());
	}

	@DisplayName("Should throw exception for actualJson null")
	@Test
	public void actualJsonNull() throws IOException {
		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			jsonSchemaComparing.isEquals(json, null);
		});
		assertEquals("JSON file must not be null!", exception.getMessage());
	}

	@DisplayName("Should throw exception for expected invalid JSON")
	@Test
	public void expectedInvalidJson() throws IOException {
		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			jsonSchemaComparing.isEquals("{ invalid: bla [", json);
		});
		assertEquals("Could not parse JSON of Swagger definitions!", exception.getMessage());
	}

	@DisplayName("Should throw exception for actual invalid JSON")
	@Test
	public void actualInvalidJson() throws IOException {
		File jsonFile= TestFileResourceLoader.getResourceAsFile("swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			jsonSchemaComparing.isEquals(json, "{ invalid: bla [");
		});
		assertEquals("Could not parse JSON of file!", exception.getMessage());
	}
}
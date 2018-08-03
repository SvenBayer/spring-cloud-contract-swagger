package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.ModelImpl;
import io.swagger.models.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResponseBodyBuilderTest {

	private ResponseBodyBuilder builder;

	@Before
	public void init() {
	    builder = new ResponseBodyBuilder();
	}

	@DisplayName("Fails for response without schema and without examples")
	@Test
	public void responseWithoutSchemaWithoutExamples() {
		Response response = new Response();
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			builder.createValueForResponseBody(response, new HashMap<>());
		});
		assertEquals("Could not parse body for response", exception.getMessage());
	}

	@DisplayName("Fails for response with broken schema and without examples")
	@Test
	public void responseWithBrokenSchemaWithoutExamples() {
		Response response = new Response();
		response.setResponseSchema(new ModelImpl());
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			builder.createValueForResponseBody(response, new HashMap<>());
		});
		assertEquals("Could not parse body for response", exception.getMessage());
	}

	@DisplayName("With ExampleSet")
	@Test
	public void withExampleSet() {
		Response response = new Response();
		HashMap<String, Object> examples = new HashMap<>();
		examples.put("key", "value");
		response.setExamples(examples);
		String actualValue = builder.createValueForResponseBody(response, new HashMap<>());
		assertEquals("value", actualValue);
	}

	@DisplayName("Response with no proper example data set")
	@Test
	public void noProperExampleDataSet() {
		Response response = new Response();
		response.setExamples(new HashMap<>());
		response.setVendorExtensions(new HashMap<>());
		response.setResponseSchema(new ModelImpl());
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			builder.createValueForResponseBody(response, new HashMap<>());
		});
		assertEquals("Could not parse body for response", exception.getMessage());
	}

	@DisplayName("Schema with no x-example data set")
	@Test
	public void schemaNoProperXExampleDataSet() {
		Response response = new Response();
		ModelImpl model = new ModelImpl();
		model.setVendorExtensions(new HashMap<>());
		model.setEnum(new ArrayList<>());
		response.setResponseSchema(model);
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			builder.createValueForResponseBody(response, new HashMap<>());
		});
		assertEquals("Could not parse body for response", exception.getMessage());
	}
}
package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.ModelImpl;
import io.swagger.models.parameters.BodyParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestBodyParamBuilderTest {

	private RequestBodyParamBuilder requestBodyParamBuilder;

	@Before
	public void init() {
		requestBodyParamBuilder = new RequestBodyParamBuilder();
	}

	@DisplayName("Fails for broken body parameter")
	@Test
	public void brokenBodyParameter() {
		BodyParameter parameter = new BodyParameter();
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			requestBodyParamBuilder.createValueForRequestBody(parameter, new HashMap<>());
		});
		assertEquals("Could not parse body for request", exception.getMessage());
	}

	@DisplayName("With ExampleSet")
	@Test
	public void withExampleSet() {
		BodyParameter parameter = new BodyParameter();
		HashMap<String, String> examples = new HashMap<>();
		examples.put("key", "value");
		parameter.setExamples(examples);
		String actualValue = requestBodyParamBuilder.createValueForRequestBody(parameter, new HashMap<>());
		assertEquals("value", actualValue);
	}

	@DisplayName("Response with no proper Example set")
	@Test
	public void noProperExampleSet() {
		BodyParameter parameter = new BodyParameter();
		parameter.setExamples(new HashMap<>());
		parameter.setVendorExtensions(new HashMap<>());
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			requestBodyParamBuilder.createValueForRequestBody(parameter, new HashMap<>());
		});
		assertEquals("Could not parse body for request", exception.getMessage());
	}

	@DisplayName("Schema with no proper Example set")
	@Test
	public void schemaNoProperExampleSet() {
		BodyParameter parameter = new BodyParameter();
		ModelImpl schema = new ModelImpl();
		schema.setEnum(new ArrayList<>());
		schema.setVendorExtensions(new HashMap<>());
		parameter.setSchema(schema);
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			requestBodyParamBuilder.createValueForRequestBody(parameter, new HashMap<>());
		});
		assertEquals("Could not parse body for request", exception.getMessage());
	}
}
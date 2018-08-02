package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.parameters.BodyParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

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
}
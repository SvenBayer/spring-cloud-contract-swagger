package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.parameters.BodyParameter;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestBodyParamBuilderTest {

	@DisplayName("Fails for broken body parameter")
	@Test
	public void brokenBodyParameter() {
		BodyParameter parameter = new BodyParameter();
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			RequestBodyParamBuilder.createValueForRequestBody(parameter, new HashMap<>());
		});
		assertEquals("Could not parse body for request", exception.getMessage());
	}
}
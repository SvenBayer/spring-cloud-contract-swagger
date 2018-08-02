package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.ModelImpl;
import io.swagger.models.Response;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResponseBodyBuilderTest {

	@DisplayName("Fails for response without schema and without examples")
	@Test
	public void responseWithoutSchemaWithoutExamples() {
		Response response = new Response();
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			ResponseBodyBuilder.createValueForResponseBody(response, new HashMap<>());
		});
		assertEquals("Could not parse body for response", exception.getMessage());
	}

	@DisplayName("Fails for response with broken schema and without examples")
	@Test
	public void responseWithBrokenSchemaWithoutExamples() {
		Response response = new Response();
		response.setResponseSchema(new ModelImpl());
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			ResponseBodyBuilder.createValueForResponseBody(response, new HashMap<>());
		});
		assertEquals("Could not parse body for response", exception.getMessage());
	}
}
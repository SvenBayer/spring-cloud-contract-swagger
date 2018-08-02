package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;


import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.parameters.QueryParameter;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.cloud.contract.spec.internal.DslProperty;

import java.util.Collections;
import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestContractEquals.assertDslPropertyEquals;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.*;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sven Bayer
 */
public class DslValueBuilderTest {

	@DisplayName("Dsl Value for Example value")
	@Test
	public void dslValueForExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setExample("anExampleValue");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile(".+"), "anExampleValue");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for X-Example value")
	@Test
	public void dslValueForXExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setVendorExtension("x-example", "anXExampleValue");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile(".+"), "anXExampleValue");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for Default value")
	@Test
	public void dslValueForDefault() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setDefault("aDefaultValue");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile(".+"), "aDefaultValue");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for Enum")
	@Test
	public void dslValueForEnum() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setEnum(Collections.singletonList("anEnum"));
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile(".+"), "anEnum");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Is null for x-ignore")
	@Test
	public void nullForXIgnore() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setVendorExtension("x-ignore", true);
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		assertNull(actualDslProperty);
	}

	@DisplayName("Throw exception for x-ignore but required parameter")
	@Test
	public void xIgnoreButRequired() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setVendorExtension("x-ignore", true);
		swaggerQueryParam.setRequired(true);
		swaggerQueryParam.setName("ignoredButRequired");
		SwaggerContractConverterException exception = assertThrows(SwaggerContractConverterException.class, () -> {
			DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		});
		assertEquals("Set the parameter 'ignoredButRequired' to required: false to use x-ignore: true", exception.getMessage());
	}

	@DisplayName("Dsl Value for string with name and no example")
	@Test
	public void stringWithNameAndNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(STRING.type());
		swaggerQueryParam.setName("nameOfParam");
		swaggerQueryParam.setPattern("[A-Za-z]*");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("[A-Za-z]*"), "nameOfParam");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for string and no example")
	@Test
	public void stringNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(STRING.type());
		swaggerQueryParam.setPattern("[A-Za-z]*");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("[A-Za-z]*"), "string");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for number and no example")
	@Test
	public void numberNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(NUMBER.type());
		swaggerQueryParam.setPattern("[0-9]+");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("[0-9]+"), DEFAULT_INT);
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for number double and no example")
	@Test
	public void numberDoubleNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(NUMBER.type());
		swaggerQueryParam.setFormat(DOUBLE.type());
		swaggerQueryParam.setPattern("[0-9]+\\.[0-9]+");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("[0-9]+\\.[0-9]+"), DEFAULT_FLOAT);
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for number float and no example")
	@Test
	public void numberFloatNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(NUMBER.type());
		swaggerQueryParam.setFormat(FLOAT.type());
		swaggerQueryParam.setPattern("[0-9]+\\.[0-9]+");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("[0-9]+\\.[0-9]+"), DEFAULT_FLOAT);
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for boolean and no example")
	@Test
	public void booleanNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(BOOLEAN.type());
		swaggerQueryParam.setPattern("(false|true)");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("(false|true)"), DEFAULT_BOOLEAN);
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}

	@DisplayName("Dsl Value for undefined type and no example")
	@Test
	public void undefinedTypeNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType("undefined");
		swaggerQueryParam.setPattern("(false|true)");
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile("(false|true)"), 1);
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}


	@DisplayName("Dsl Value for string and not pattern")
	@Test
	public void stringNoPatternNoExample() {
		QueryParameter swaggerQueryParam = new QueryParameter();
		swaggerQueryParam.setType(STRING.type());
		DslProperty<Object> actualDslProperty = DslValueBuilder.createDslValueForParameter(swaggerQueryParam);
		DslProperty<Object> expectedDslProperty = new DslProperty<>(Pattern.compile(".+"), "string");
		assertDslPropertyEquals(expectedDslProperty, actualDslProperty, "DslProperty was not equals!");
	}
}
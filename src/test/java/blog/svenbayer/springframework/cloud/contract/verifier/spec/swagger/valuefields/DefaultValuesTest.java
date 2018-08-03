package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFormats.*;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.INTEGER;
import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.NUMBER;
import static org.junit.Assert.assertEquals;

public class DefaultValuesTest {

	private DefaultValues defaultValues;

	@Before
	public void init() {
		defaultValues = new DefaultValues();
	}

	@DisplayName("Double with min")
	@Test
	public void doubleWithMin() {
		Object actualValue = defaultValues.createDefaultValueForType(NUMBER.type(), DOUBLE.format(), "myValue", BigDecimal.valueOf(3.4d), null);
		assertEquals(3.4d, actualValue);
	}

	@DisplayName("Float with max")
	@Test
	public void floatWithMax() {
		Object actualValue = defaultValues.createDefaultValueForType(NUMBER.type(), FLOAT.format(), "myValue", null, BigDecimal.valueOf(3.4f));
		assertEquals(3.4f, actualValue);
	}

	@DisplayName("Default Long")
	@Test
	public void defaultLong() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), INT_64.format(), "myValue", null, null);
		assertEquals(1L, actualValue);
	}

	@DisplayName("Long with max")
	@Test
	public void longWithMax() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), INT_64.format(), "myValue", null, BigDecimal.valueOf(4));
		assertEquals(4L, actualValue);
	}

	@DisplayName("Long with min")
	@Test
	public void longWithMin() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), INT_64.format(), "myValue", BigDecimal.valueOf(4), null);
		assertEquals(4L, actualValue);
	}

	@DisplayName("Default Integer")
	@Test
	public void defaultInteger() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), null, "myValue", null, null);
		assertEquals(1, actualValue);
	}

	@DisplayName("Default Integer32")
	@Test
	public void defaultInteger32() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), INT_32.format(), "myValue", null, null);
		assertEquals(1, actualValue);
	}

	@DisplayName("Integer with max")
	@Test
	public void integerWithMax() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), null, "myValue", null, BigDecimal.valueOf(4));
		assertEquals(4, actualValue);
	}

	@DisplayName("Integer with min")
	@Test
	public void integerWithMin() {
		Object actualValue = defaultValues.createDefaultValueForType(INTEGER.type(), null, "myValue", BigDecimal.valueOf(4), null);
		assertEquals(4, actualValue);
	}
}
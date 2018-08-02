package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import io.swagger.models.properties.*;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class ResponseHeaderValueBuilderTest {

	@DisplayName("Default value for Double")
	@Test
	public void defaultValueForDouble() {
		DoubleProperty property = new DoubleProperty();
		property.setDefault(3.2d);
		Object defaultValue = ResponseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(3.2d, defaultValue);
	}

	@DisplayName("Default value for Float")
	@Test
	public void defaultValueForFloat() {
		FloatProperty property = new FloatProperty();
		property.setDefault(3.3f);
		Object defaultValue = ResponseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(3.3f, defaultValue);
	}

	@DisplayName("Default value for Long")
	@Test
	public void defaultValueForLong() {
		LongProperty property = new LongProperty();
		property.setDefault(4L);
		Object defaultValue = ResponseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(4L, defaultValue);
	}

	@DisplayName("Default value for Integer")
	@Test
	public void defaultValueForInteger() {
		IntegerProperty property = new IntegerProperty();
		property.setDefault(5);
		Object defaultValue = ResponseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(5, defaultValue);
	}

	@DisplayName("Default value for Boolean")
	@Test
	public void defaultValueForBoolean() {
		BooleanProperty property = new BooleanProperty();
		property.setDefault(false);
		Object defaultValue = ResponseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(false, defaultValue);
	}

	@DisplayName("Default value for String")
	@Test
	public void defaultValueForString() {
		StringProperty property = new StringProperty();
		property.setDefault("a text");
		Object defaultValue = ResponseHeaderValueBuilder.getDefaultValue(property);
		assertEquals("a text", defaultValue);
	}

	@DisplayName("Typed numeric value for Long")
	@Test
	public void typedNumericForLong() {
		LongProperty property = new LongProperty();
		BigDecimal value = new BigDecimal(5);
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, value);
		assertEquals(5L, actualValue);
	}

	@DisplayName("Typed numeric value for Integer")
	@Test
	public void typedNumericForInteger() {
		IntegerProperty property = new IntegerProperty();
		BigDecimal value = new BigDecimal(8);
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, value);
		assertEquals(8, actualValue);
	}

	@DisplayName("Typed numeric value for BaseInteger")
	@Test
	public void typedNumericForBaseInteger() {
		BaseIntegerProperty property = new BaseIntegerProperty();
		BigDecimal value = new BigDecimal(9);
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, value);
		assertEquals(9, actualValue);
	}

	@DisplayName("Typed numeric value for Double")
	@Test
	public void typedNumericForDouble() {
		DoubleProperty property = new DoubleProperty();
		BigDecimal value = new BigDecimal(3.21d);
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, value);
		assertEquals(3.21d, actualValue);
	}

	@DisplayName("Typed numeric value for Float")
	@Test
	public void typedNumericForFloat() {
		FloatProperty property = new FloatProperty();
		BigDecimal value = new BigDecimal(2.67f);
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, value);
		assertEquals(2.67f, actualValue);
	}

	@DisplayName("Typed numeric value for Decimal")
	@Test
	public void typedNumericForDecimal() {
		DecimalProperty property = new DecimalProperty();
		BigDecimal value = new BigDecimal(4);
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, value);
		assertEquals(new BigDecimal(4), actualValue);
	}

	@DisplayName("Typed numeric value for no default value")
	@Test
	public void typedNumericForNoDefaultValue() {
		DecimalProperty property = new DecimalProperty();
		Object actualValue = ResponseHeaderValueBuilder.getTypedNumericValue(property, null);
		assertEquals(1, actualValue);
	}
}
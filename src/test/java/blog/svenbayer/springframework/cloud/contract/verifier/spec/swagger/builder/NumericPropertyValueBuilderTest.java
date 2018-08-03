package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import io.swagger.models.properties.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class NumericPropertyValueBuilderTest {

	private NumericPropertyValueBuilder builder;

	@Before
	public void init() {
		builder = new NumericPropertyValueBuilder();
	}

	@DisplayName("Typed numeric value for Long")
	@Test
	public void typedNumericForLong() {
		LongProperty property = new LongProperty();
		BigDecimal value = BigDecimal.valueOf(5);
		Object actualValue = builder.getTypedNumericValue(property, value);
		assertEquals(5L, actualValue);
	}

	@DisplayName("Typed numeric value for Integer")
	@Test
	public void typedNumericForInteger() {
		IntegerProperty property = new IntegerProperty();
		BigDecimal value = BigDecimal.valueOf(8);
		Object actualValue = builder.getTypedNumericValue(property, value);
		assertEquals(8, actualValue);
	}

	@DisplayName("Typed numeric value for BaseInteger")
	@Test
	public void typedNumericForBaseInteger() {
		BaseIntegerProperty property = new BaseIntegerProperty();
		BigDecimal value = BigDecimal.valueOf(9);
		Object actualValue = builder.getTypedNumericValue(property, value);
		assertEquals(9, actualValue);
	}

	@DisplayName("Typed numeric value for Double")
	@Test
	public void typedNumericForDouble() {
		DoubleProperty property = new DoubleProperty();
		BigDecimal value = BigDecimal.valueOf(3.21d);
		Object actualValue = builder.getTypedNumericValue(property, value);
		assertEquals(3.21d, actualValue);
	}

	@DisplayName("Typed numeric value for Float")
	@Test
	public void typedNumericForFloat() {
		FloatProperty property = new FloatProperty();
		BigDecimal value = BigDecimal.valueOf(2.67f);
		Object actualValue = builder.getTypedNumericValue(property, value);
		assertEquals(2.67f, actualValue);
	}

	@DisplayName("Typed numeric value for Decimal")
	@Test
	public void typedNumericForDecimal() {
		DecimalProperty property = new DecimalProperty();
		BigDecimal value = BigDecimal.valueOf(4);
		Object actualValue = builder.getTypedNumericValue(property, value);
		assertEquals(BigDecimal.valueOf(4), actualValue);
	}

	@DisplayName("Typed numeric value for no default value")
	@Test
	public void typedNumericForNoDefaultValue() {
		IntegerProperty property = new IntegerProperty();
		Object actualValue = builder.getTypedNumericValue(property, null);
		assertEquals(1, actualValue);
	}

	@DisplayName("Value for unknown property type")
	@Test
	public void unknownPropertyType() {
		AbstractNumericProperty property = new AbstractNumericProperty() {};
		Object actualValue = builder.getTypedNumericValue(property, BigDecimal.valueOf(1));
		assertEquals(1, actualValue);
	}
}
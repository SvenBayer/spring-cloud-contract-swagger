package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import io.swagger.models.properties.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.DefaultValues.DEFAULT_INT;
import static org.junit.Assert.assertEquals;

public class ResponseHeaderValueBuilderTest {

	private ResponseHeaderValueBuilder responseHeaderValueBuilder;

	@Before
	public void init() {
		responseHeaderValueBuilder = new ResponseHeaderValueBuilder();
	}

	@DisplayName("Default value for Double")
	@Test
	public void defaultValueForDouble() {
		DoubleProperty property = new DoubleProperty();
		property.setDefault(3.2d);
		Object defaultValue = responseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(3.2d, defaultValue);
	}

	@DisplayName("Default value for Float")
	@Test
	public void defaultValueForFloat() {
		FloatProperty property = new FloatProperty();
		property.setDefault(3.3f);
		Object defaultValue = responseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(3.3f, defaultValue);
	}

	@DisplayName("Default value for Long")
	@Test
	public void defaultValueForLong() {
		LongProperty property = new LongProperty();
		property.setDefault(4L);
		Object defaultValue = responseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(4L, defaultValue);
	}

	@DisplayName("Default value for Integer")
	@Test
	public void defaultValueForInteger() {
		IntegerProperty property = new IntegerProperty();
		property.setDefault(5);
		Object defaultValue = responseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(5, defaultValue);
	}

	@DisplayName("Default value for Boolean")
	@Test
	public void defaultValueForBoolean() {
		BooleanProperty property = new BooleanProperty();
		property.setDefault(false);
		Object defaultValue = responseHeaderValueBuilder.getDefaultValue(property);
		assertEquals(false, defaultValue);
	}

	@DisplayName("Default value for String")
	@Test
	public void defaultValueForString() {
		StringProperty property = new StringProperty();
		property.setDefault("a text");
		Object defaultValue = responseHeaderValueBuilder.getDefaultValue(property);
		assertEquals("a text", defaultValue);
	}

	@DisplayName("Response header for ArrayProperty")
	@Test
	public void createResponseHeaderValue() {
		ArrayProperty property = new ArrayProperty();
		Object defaultValue = responseHeaderValueBuilder.createResponseHeaderValue("key", property, new HashMap<>());
		assertEquals(new ArrayList<>(Collections.singleton(DEFAULT_INT)), defaultValue);
	}
}
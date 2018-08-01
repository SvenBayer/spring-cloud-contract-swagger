package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sven Bayer
 */
public class PatternBuilderTest {

	@DisplayName("String matches pattern")
	@Test
	public void stringMatchesPattern() {
		Pattern pattern = PatternBuilder.createPatternForParameter(STRING.type(), null);
		assertTrue(pattern.matcher("SomeString").matches());
	}

	@DisplayName("Double matches pattern")
	@Test
	public void numberDoubleMatchesPattern() {
		Pattern pattern = PatternBuilder.createPatternForParameter(NUMBER.type(), DOUBLE.type());
		assertTrue(pattern.matcher("1.1").matches());
	}

	@DisplayName("Float matches pattern")
	@Test
	public void numberFloatMatchesPattern() {
		Pattern pattern = PatternBuilder.createPatternForParameter(NUMBER.type(), FLOAT.type());
		assertTrue(pattern.matcher("1.1").matches());
	}

	@DisplayName("Number matches pattern")
	@Test
	public void numberMatchesPattern() {
		Pattern pattern = PatternBuilder.createPatternForParameter(NUMBER.type(), null);
		assertTrue(pattern.matcher("1").matches());
	}

	@DisplayName("Boolean matches pattern")
	@Test
	public void booleanMatchesPattern() {
		Pattern pattern = PatternBuilder.createPatternForParameter(BOOLEAN.type(), null);
		assertTrue(pattern.matcher("false").matches());
	}
}
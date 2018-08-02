package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sven Bayer
 */
public class PatternBuilderTest {

	private PatternBuilder patternBuilder;

	@Before
	public void init() {
		patternBuilder = new PatternBuilder();
	}

	@DisplayName("String matches pattern")
	@Test
	public void stringMatchesPattern() {
		Pattern pattern = patternBuilder.createPatternForParameter(STRING.type(), null);
		assertTrue(pattern.matcher("SomeString").matches());
	}

	@DisplayName("Double matches pattern")
	@Test
	public void numberDoubleMatchesPattern() {
		Pattern pattern = patternBuilder.createPatternForParameter(NUMBER.type(), DOUBLE.type());
		assertTrue(pattern.matcher("1.1").matches());
	}

	@DisplayName("Float matches pattern")
	@Test
	public void numberFloatMatchesPattern() {
		Pattern pattern = patternBuilder.createPatternForParameter(NUMBER.type(), FLOAT.type());
		assertTrue(pattern.matcher("1.1").matches());
	}

	@DisplayName("Number matches pattern")
	@Test
	public void numberMatchesPattern() {
		Pattern pattern = patternBuilder.createPatternForParameter(NUMBER.type(), null);
		assertTrue(pattern.matcher("1").matches());
	}

	@DisplayName("Boolean matches pattern")
	@Test
	public void booleanMatchesPattern() {
		Pattern pattern = patternBuilder.createPatternForParameter(BOOLEAN.type(), null);
		assertTrue(pattern.matcher("false").matches());
	}
}
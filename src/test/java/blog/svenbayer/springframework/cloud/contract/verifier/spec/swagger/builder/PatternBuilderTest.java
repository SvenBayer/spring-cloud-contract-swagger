package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.STRING;
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
}
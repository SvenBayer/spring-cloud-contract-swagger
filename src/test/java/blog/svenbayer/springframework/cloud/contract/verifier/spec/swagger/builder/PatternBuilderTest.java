package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerTypes.STRING;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sven Bayer
 */
class PatternBuilderTest {

	@DisplayName("String matches pattern")
	@Test
	void stringMatchesPattern() {
		Pattern pattern = PatternBuilder.createPatternForParameter(STRING.type(), null);
		assertTrue(pattern.matcher("SomeString").matches());
	}
}
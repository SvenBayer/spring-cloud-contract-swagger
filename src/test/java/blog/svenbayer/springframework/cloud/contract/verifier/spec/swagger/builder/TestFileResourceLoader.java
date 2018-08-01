package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

/**
 * @author Sven Bayer
 */
public class TestFileResourceLoader {

	public static File getResourceAsFile(String resourcePath) {
		ClassLoader classLoader = TestFileResourceLoader.class.getClassLoader();
		URL resource = classLoader.getResource(resourcePath);
		if (resource == null) {
			throw new IllegalStateException("Resource should not be null but was for '" + resourcePath + "'");
		}
		return new File(resource.getFile());
	}

	public static String getResourceAsString(String resourcePath) throws IOException {
		return new String(Files.readAllBytes(getResourceAsFile(resourcePath).toPath()));
	}
}

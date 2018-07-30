package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger;

import java.nio.file.Path;

public class SwaggerFileFolder {

	private static Path swaggerFileFolder;

	public static Path getSwaggerFileFolder() {
		return swaggerFileFolder;
	}

	public static void setSwaggerFileFolder(Path swaggerFileFolder) {
		SwaggerFileFolder.swaggerFileFolder = swaggerFileFolder;
	}
}

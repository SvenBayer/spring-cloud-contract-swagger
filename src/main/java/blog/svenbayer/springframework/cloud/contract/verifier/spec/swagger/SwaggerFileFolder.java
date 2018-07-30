package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger;

import java.nio.file.Path;

public class SwaggerFileFolder {

	private static Path pathToSwaggerFile;

	private SwaggerFileFolder() {
	}

	public static Path getPathToSwaggerFile() {
		return pathToSwaggerFile;
	}

	public static void setPathToSwaggerFile(Path pathToSwaggerFile) {
		SwaggerFileFolder.pathToSwaggerFile = pathToSwaggerFile;
	}
}

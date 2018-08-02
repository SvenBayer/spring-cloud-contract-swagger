package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger;

import java.nio.file.Path;

/**
 * Stores the location of the Swagger file.
 *
 * @author Sven Bayer
 */
public class SwaggerFileFolder {

	private static SwaggerFileFolder swaggerFileFolder;
	private Path pathToSwaggerFile;

	private SwaggerFileFolder() {
	}

	public static SwaggerFileFolder instance() {
		if (swaggerFileFolder == null) {
			swaggerFileFolder = new SwaggerFileFolder();
		}
		return swaggerFileFolder;
	}

	public Path getPathToSwaggerFile() {
		return this.pathToSwaggerFile;
	}

	public void setPathToSwaggerFile(Path pathToSwaggerFile) {
		this.pathToSwaggerFile = pathToSwaggerFile;
	}
}

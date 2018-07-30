package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.SwaggerFileFolder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Sven Bayer
 */
public class JsonFileResolverSwagger implements SwaggerReferenceResolver {

	private String reference;

	public JsonFileResolverSwagger(String reference) {
		this.reference = reference;
	}

	@Override
	public String resolveReference(Map<String, Model> definitions) {
		Path swaggerFileFolder = SwaggerFileFolder.getSwaggerFileFolder();
		File pathToRef = new File(swaggerFileFolder.toString(), reference);
		if (!pathToRef.exists()) {
			throw new SwaggerContractConverterException("Swagger file must only reference files that exist. Could not find file '" + reference + "'");
		}
		try {
			return new String(Files.readAllBytes(pathToRef.toPath()));
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not read external file '" + reference + "'");
		}
	}
}

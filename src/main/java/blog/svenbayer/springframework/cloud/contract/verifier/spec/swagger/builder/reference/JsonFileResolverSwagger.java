package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.SwaggerFileFolder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json.JsonSchemaComparer;
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

	private String referenceFile;
	private SwaggerDefinitionsRefResolverSwagger refResolverSwagger;
	private JsonSchemaComparer jsonSchemaComparer = new JsonSchemaComparer();

	JsonFileResolverSwagger(String referenceFile, String reference) {
		this.referenceFile = referenceFile;
		this.refResolverSwagger = new SwaggerDefinitionsRefResolverSwagger(reference);
	}

	@Override
	public String resolveReference(Map<String, Model> definitions) {
		Path swaggerFileFolder = SwaggerFileFolder.getPathToSwaggerFile();
		File pathToRef = new File(swaggerFileFolder.toString(), this.referenceFile);
		if (!pathToRef.exists() || pathToRef.isDirectory()) {
			throw new SwaggerContractConverterException("Swagger file must only referenceFile files that exist. Could not find file '" + pathToRef + "'");
		}
		String externalJson;
		try {
			externalJson = new String(Files.readAllBytes(pathToRef.toPath()));
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not read external file '" + this.referenceFile + "'", e);
		}
		validateExternalJson(externalJson, definitions);
		return externalJson;
	}

	private void validateExternalJson(String externalJson, Map<String, Model> definitions) {
		if (definitions == null || definitions.isEmpty()) {
			return;
		}
		String resolvedJson = this.refResolverSwagger.resolveReference(definitions);
		boolean isJsonEquals = jsonSchemaComparer.isEquals(resolvedJson, externalJson);
		if (!isJsonEquals) {
			throw new SwaggerContractConverterException("Swagger definitions and Json file should be equal but was not for:\nExpected:\n"
			+ resolvedJson + "\n\nActual:\n" + externalJson);
		}
	}
}

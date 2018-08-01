package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.SwaggerFileFolder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json.JsonSchemaComparing;
import io.swagger.models.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Resolves external Json files that are referenced with x-ref fields in a Swagger document.
 *
 * @author Sven Bayer
 */
public class JsonFileResolverSwagger implements SwaggerReferenceResolver {

	private String referenceFile;
	private SwaggerDefinitionsRefResolverSwagger refResolverSwagger;
	private JsonSchemaComparing jsonSchemaComparing = new JsonSchemaComparing();

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

	/**
	 * Validates if the given Json from the external Json file matches the Swagger model definitions.
	 *
	 * @param externalJson the external Json
	 * @param definitions the Swagger model definitions
	 */
	private void validateExternalJson(String externalJson, Map<String, Model> definitions) {
		if (definitions == null || definitions.isEmpty()) {
			return;
		}
		String resolvedJson = this.refResolverSwagger.resolveReference(definitions);
		boolean isJsonEquals = this.jsonSchemaComparing.isEquals(resolvedJson, externalJson);
		if (!isJsonEquals) {
			throw new SwaggerContractConverterException("Swagger definitions and Json file should be equal but was not for:\nExpected:\n"
			+ resolvedJson + "\n\nActual:\n" + externalJson);
		}
	}
}

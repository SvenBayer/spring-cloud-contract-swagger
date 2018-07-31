package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.SwaggerFileFolder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private String reference;
	private SwaggerDefinitionsRefResolverSwagger refResolverSwagger;

	public JsonFileResolverSwagger(String referenceFile, String reference) {
		this.referenceFile = referenceFile;
		this.reference = reference;
		this.refResolverSwagger = new SwaggerDefinitionsRefResolverSwagger(reference);
	}

	@Override
	public String resolveReference(Map<String, Model> definitions) {
		Path swaggerFileFolder = SwaggerFileFolder.getPathToSwaggerFile();
		File pathToRef = new File(swaggerFileFolder.toString(), this.referenceFile);
		if (!pathToRef.exists()) {
			throw new SwaggerContractConverterException("Swagger file must only referenceFile files that exist. Could not find file '" + this.referenceFile + "'");
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
		String resolvedJson = this.refResolverSwagger.resolveReference(definitions);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode resolvedJsonNode;
		JsonNode externalJsonNode;
		try {
			resolvedJsonNode = mapper.readTree(resolvedJson);
			externalJsonNode = mapper.readTree(externalJson);
		} catch (IOException e) {
			throw new SwaggerContractConverterException("Could not validate json schema!", e);
		}
		resolvedJsonNode.fieldNames().forEachRemaining(name -> {
			if(externalJsonNode.findValues(name).isEmpty()) {
				throw new SwaggerContractConverterException("External JSON file '" + externalJson + "' does not contain field '" + name + "' in Swagger definition '" + this.reference + "'");
			}
		});
		externalJsonNode.fieldNames().forEachRemaining(name -> {
			if (resolvedJsonNode.findValues(name).isEmpty()) {
				throw new SwaggerContractConverterException("Swagger Definitions '" + this.reference + "' does not contain field'" + name + "' in external JSON file '" + externalJson + "'");
			}
		});
	}
}

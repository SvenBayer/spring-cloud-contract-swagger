package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder

import io.swagger.models.HttpMethod
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

class PathLinkBuilderTest extends Specification {
    def "Escapes path for Contract name with path parameter and slashes"() {
        given:
            String pathName = "/find/planets/{solarSystem}/{system}"
            AtomicInteger priority = new AtomicInteger(1)
            HttpMethod post = HttpMethod.POST
        when:
            String contractName = PathLinkBuilder.createContractName(priority, pathName, post)
        then:
            contractName == "1_find_planets_solarSystem_system_POST"
    }
}

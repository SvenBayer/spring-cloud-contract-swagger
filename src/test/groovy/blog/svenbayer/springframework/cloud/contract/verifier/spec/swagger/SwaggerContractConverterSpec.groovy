package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException
import io.swagger.models.Swagger
import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.DslProperty
import spock.lang.Specification
import spock.lang.Subject

import java.util.regex.Pattern

/**
 * @author Sven Bayer
 */
class SwaggerContractConverterSpec extends Specification {

    @Subject SwaggerContractConverter converter = new SwaggerContractConverter()

    def "should accept yaml files that are swagger files"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_swagger.yml").toURI())
        expect:
            converter.isAccepted(singleSwaggerYaml)
    }

    def "should reject yaml files that are swagger files"() {
        given:
            File invalidSwagger = new File(SwaggerContractConverterSpec.getResource("/swagger/invalid_swagger.yml").toURI())
        expect:
            !converter.isAccepted(invalidSwagger)
    }

    def "should convert from single swagger to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
                }
                response {
                    status(201)
                    headers {
                        header("X-RateLimit-Limit", 1)
                        contentType(allValue())
                    }
                    body(
"""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
  "name" : "name"
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString().normalize() == [expectedContract].toString().normalize()
    }

    def "should convert from swagger with min and max values to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/numeric_min_max_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
                }
                response {
                    status(201)
                    headers {
                        header("X-RateLimit-Limit", 1)
                        header("wormholeCount", 11)
                        header("hyperLoopCount", 4)
                        header("draculaCount", 3)
                        contentType(allValue())
                    }
                    body(
"""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
  "name" : "name"
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString().normalize() == [expectedContract].toString().normalize()
    }

    def "should convert from swagger with json example to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/json_example_param_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{ "beanonauts": [ { "name": "Tom Hanks", "age": 62.0 }], "fuel": 300000.5, "weight": 45931.0, "itinerary": { "destination": "Moon", "departure": "Earth" }, "rocketName": "Beanpollo 13" }""")
                }
                response {
                    status(201)
                    headers {
                        header("X-RateLimit-Limit", 1)
                        contentType(allValue())
                    }
                    body(
"""{ "asteroids" : [ { "shape" : "BEAN", "aliens" : [ { "heads" : [ "big ones" ] } ], "name" : "Beansteroid", "speed" : 250, "istransparent" : false } ], "size" : 1, "name" : "Beanlet" }""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString().normalize() == [expectedContract].toString().normalize()
    }

    def "should convert from x-ignore fields of swagger to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/ignored_param_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        contentType(applicationJson())
                    }
                    body(
"""{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
                }
                response {
                    status(201)
                    headers {
                        header("X-RateLimit-Limit", 1)
                        contentType(allValue())
                    }
                    body(
"""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
  "name" : "name"
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString().normalize() == [expectedContract].toString().normalize()
    }

    def "should expect exception for required param with x-ignore field set to true"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/ignored_required_param_swagger.yml").toURI())
        when:
            converter.convertFrom(singleSwaggerYaml)
        then:
            thrown SwaggerContractConverterException
    }

    def "should convert from single swagger with path param to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_pathparam_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_rocket_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff/rocket") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(applicationJson())
                    }
                    body(
"""{
  "name" : "name"
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString().normalize() == [expectedContract].toString().normalize()
    }

    def "should convert from single parametrized swagger to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/param_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", false)
                            parameter("viaHyperLoop", false)
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "beanonauts" : [ {
    "name" : "Beanon Beanusk",
    "age" : 47
  } ],
  "fuel" : 980.3,
  "weight" : 20.85,
  "itinerary" : {
    "destination" : "Mars",
    "departure" : "Earth"
  },
  "rocketName" : "BeanRocket Heavy"
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(allValue())
                    }
                    body(
                            """{
  "asteroids" : [ {
    "shape" : "BEAN",
    "name" : "Phobos",
    "speed" : 23
  } ],
  "size" : 6779,
  "name" : "Mars"
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString().normalize() == [expectedContract].toString().normalize()
    }

    def "should convert from multiple swagger to contract"() {
        given:
            File multipleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/multiple_swagger.yml").toURI())
            Contract expectedContract0 = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("1_takeoff_POST")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(allValue())
                    }
                    body(
                            """{
  "asteroids" : [ {
    "shape" : "ROUND",
    "name" : "name",
    "speed" : 1
  } ],
  "size" : 1,
  "name" : "name"
}""")
                }
            }
            Contract expectedContract1 = Contract.make {
                label("land")
                name("3_land_POST")
                description("Lands a coffee rocket on a bean planet and returns the coffee rocket.")
                priority(3)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/land") {
                        queryParameters {
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "asteroids" : [ {
    "shape" : "ROUND",
    "name" : "name",
    "speed" : 1
  } ],
  "size" : 1,
  "name" : "name"
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
                }
            }
            Contract expectedContract2 = Contract.make {
                name("4_find_planets_solarSystem_GET")
                description("Find planets in the given Solar System.")
                priority(4)
                request {
                    method(GET())
                    urlPath("/coffee-rocket-service/v1.0/find/planets/solarSystem") {
                        queryParameters {
                            parameter("planetName", new DslProperty(Pattern.compile(".+"), "planetName"))
                            parameter("numAsteroids", new DslProperty(Pattern.compile("[0-9]+"), 1))
                            parameter("minSize", new DslProperty(Pattern.compile("[0-9]+"), 1))
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                        contentType(applicationJson())
                    }
                }
                response {
                    status(200)
                    headers {
                        contentType(allValue())
                    }
                    body(
                            """{
  "asteroids" : [ {
    "shape" : "ROUND",
    "name" : "name",
    "speed" : 1
  } ],
  "size" : 1,
  "name" : "name"
}""")
                }
            }
            Contract expectedContract3 = Contract.make {
                name("5_planets_planet_asteroids_asteroidName_GET")
                description("Retrieve existing bean asteroids from a bean planet.")
                priority(5)
                request {
                    method(GET())
                    urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                        queryParameters {
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    }
                }
                response {
                    status(200)
                    headers {
                        contentType(allValue())
                    }
                    body(
                            """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
                }
            }
        Contract expectedContract4 = Contract.make {
            name("6_planets_planet_asteroids_asteroidName_PUT")
            description("Updates an existing bean asteroids of a bean planet.")
            priority(6)
            request {
                method(PUT())
                urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
            }
            response {
                status(200)
                headers {
                    contentType(allValue())
                }
                body(
                        """{
  "x" : 1,
  "y" : 1
}""")
            }
        }
            Contract expectedContract5 = Contract.make {
                name("7_planets_planet_asteroids_asteroidName_POST")
                description("Adds a bean asteroids to a bean planet.")
                priority(7)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                        queryParameters {
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
                }
                response {
                    status(200)
                    headers {
                        contentType(allValue())
                    }
                    body(
                            """{
  "x" : 1,
  "y" : 1
}""")
                }
            }
        Contract expectedContract6 = Contract.make {
                name("8_planets_planet_asteroids_asteroidName_DELETE")
                description("Removes an existing bean asteroids from a bean planet.")
                priority(8)
                request {
                    method(DELETE())
                    urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                        queryParameters {
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    }
                }
                response {
                    status(204)
                    headers {
                        contentType(allValue())
                    }
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(multipleSwaggerYaml)
        then:
            contracts.size() == 7
            contracts.getAt(0).toString().normalize() == expectedContract0.toString().normalize()
            contracts.getAt(1).toString().normalize() == expectedContract1.toString().normalize()
            contracts.getAt(2).toString().normalize() == expectedContract2.toString().normalize()
            contracts.getAt(3).toString().normalize() == expectedContract3.toString().normalize()
            contracts.getAt(4).toString().normalize() == expectedContract4.toString().normalize()
            contracts.getAt(5).toString().normalize() == expectedContract5.toString().normalize()
            contracts.getAt(6).toString().normalize() == expectedContract6.toString().normalize()
            contracts.toString().normalize() == [expectedContract0, expectedContract1, expectedContract2, expectedContract3, expectedContract4, expectedContract5, expectedContract6].toString().normalize()
    }

    def "should retrieve empty contract when converting from swagger"() {
        given:
            List<Contract> springCloudContracts = new ArrayList<>()
        when:
            Swagger swagger = converter.convertTo(springCloudContracts)
        then:
            swagger != null
    }
}

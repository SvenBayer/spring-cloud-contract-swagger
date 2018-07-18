# Spring Cloud Contract Swagger
**Converts Swagger files to contracts for Spring Cloud Contract**

This project enables Spring Cloud Contract to parse [Swagger API 2.0 specifications](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md) as Spring Cloud Contracts.

## Usage
You can check out [spring-cloud-contract-swagger-sample](https://github.com/SvenBayer/spring-cloud-contract-swagger-sample) for examples.

### Producer
To convert a Swagger file to a Spring Cloud Contract and to execute it against a Producer, add the **spring-cloud-contract-maven-plugin** as plugin and the converter **spring-cloud-contract-swagger** as plugin-dependency.

### Consumer
For the consumer, add as dependencies the **spring-cloud-starter-contract-stub-runner** and the converter **spring-cloud-contract-swagger**.

## Behaviour of the Converter
Currently, **Spring Cloud Contract Swagger** generates default values for a Swagger document’s fields.

* Boolean: true
* Number: 1
* Float/Double: 1.1
* String: (the name of the String)

To set your own default values, you can use the **x-example** field in the **parameters** and **responses** section. In the **definitions** section, you can also use the **x-example** or the supported **example** field. You should avoid the **default** field, since the current Swagger parser (1.0.36) interprets numerical values of **default** fields as String. Regarding the order, the converter will first evaluate **example**, then **x-example**, and then **default** fields. If it does not find a predefined value, it will go all the way down to the primitive fields. The converter will only use the first response of a Swagger method entry.

## Further Information
Read the blog about [Consumer Driven Contracts with Swagger](https://svenbayer.blog/cdc-with-swagger) for more information.

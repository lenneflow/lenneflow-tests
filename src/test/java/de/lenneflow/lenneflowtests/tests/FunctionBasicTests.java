package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.model.Function;
import de.lenneflow.lenneflowtests.model.JsonSchema;
import de.lenneflow.lenneflowtests.util.FunctionValueProvider;
import de.lenneflow.lenneflowtests.util.TestDataGenerator;
import de.lenneflow.lenneflowtests.util.TestHelper;
import de.lenneflow.lenneflowtests.util.Util;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {FunctionValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FunctionBasicTests {

    static String savedJsonInputSchemaUid;
    static String savedJsonOutputSchemaUid;
    static String savedFunctionUid;

    @Autowired
    FunctionValueProvider functionValueProvider;


    final TestHelper testHelper = new TestHelper();

    @BeforeAll
    void setUp() {
        testHelper.deleteAllFunctionsTables(functionValueProvider);
        testHelper.deleteAllFunctionsJsonSchema(functionValueProvider);
    }


    @Test
    @Order(10)
    void testCreateJsonSchema() throws IOException {
        JsonSchema inputSchema = new TestDataGenerator().generateFunctionJsonSchema("randomInputSchema");
        JsonSchema outputSchema = new TestDataGenerator().generateFunctionJsonSchema("randomOutputSchema");
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getCreateJsonSchemaPath();
        JsonSchema body = given()
                .body(inputSchema)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(inputSchema.getName()))
                .extract().body().as(JsonSchema.class);
        savedJsonInputSchemaUid = body.getUid();
        Util.pause(1000);

        JsonSchema body2 = given()
                .body(outputSchema)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("schemaVersion", equalTo(outputSchema.getSchemaVersion().name()))
                .extract().body().as(JsonSchema.class);
        savedJsonOutputSchemaUid = body2.getUid();
        Util.pause(1000);
    }

    @Test
    @Order(20)
    void testGetJsonSchema() throws IOException {
        JsonSchema inputSchema = new TestDataGenerator().generateFunctionJsonSchema("randomInputSchema");
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindJsonSchemaPath().replace("{uid}", savedJsonInputSchemaUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(inputSchema.getName()));
    }

    @Test
    @Order(30)
    void testCreateLazyFunction() throws IOException {
        Function function = new TestDataGenerator().generateFunction("dummyFunctionRandom", savedJsonInputSchemaUid, savedJsonOutputSchemaUid, true);
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getCreateFunctionPath();
        Function body = given()
                .body(function)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("lazyDeployment", equalTo(true))
                .body("deploymentState", equalTo("UNDEPLOYED"))
                .extract().body().as(Function.class);
        savedFunctionUid = body.getUid();
        Util.pause(1000);
    }

    @Test
    @Order(40)
    void testGetFunction() throws IOException {
        Function function = new TestDataGenerator().generateFunction("dummyFunctionRandom", savedJsonInputSchemaUid, savedJsonOutputSchemaUid, true);
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindFunctionPath().replace("{uid}", savedFunctionUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(function.getName()));
    }

    @Test
    @Order(50)
    void testCreateNotLazyFunction() throws IOException {
        JsonSchema inputSchema = testHelper.createJsonSchema(functionValueProvider, "sleepInputSchema");
        JsonSchema outputSchema = testHelper.createJsonSchema(functionValueProvider, "sleepOutputSchema");
        Function function = new TestDataGenerator().generateFunction("dummyFunctionSleep", inputSchema.getUid(), outputSchema.getUid(), true);
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getCreateFunctionPath();
        Function body = given()
                .body(function)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("lazyDeployment", equalTo(true)) //TODO
                .extract().body().as(Function.class);
        Util.pause(10000);
        String urlGet = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindFunctionPath().replace("{uid}", body.getUid());
        given()
                .when()
                .get(urlGet)
                .then()
                .statusCode(200)
                .body("deploymentState", equalTo("DEPLOYED"));
    }


}

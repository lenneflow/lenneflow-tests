package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.model.*;
import de.lenneflow.lenneflowtests.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {WorkflowValueProvider.class, FunctionValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkflowBasicTests {

    static String savedJsonInputSchemaUid;
    static String savedJsonOutputSchemaUid;
    static String savedWorkflowUid;

    final TestHelper testHelper = new TestHelper();

    @Autowired
    private WorkflowValueProvider workflowValueProvider;

    @Autowired
    private FunctionValueProvider functionValueProvider;

    @BeforeAll
    void setUp() {
        testHelper.deleteAllWorkflowsJsonSchema(workflowValueProvider);
        testHelper.deleteAllWorkflows(workflowValueProvider);
        testHelper.deleteAllWorkflowSteps(workflowValueProvider);
    }


    @Test
    @Order(10)
    void testCreateJsonSchema() throws IOException {
        JsonSchema inputSchema = new TestDataGenerator().generateFunctionJsonSchema("randomInputSchema");
        JsonSchema outputSchema = new TestDataGenerator().generateFunctionJsonSchema("randomOutputSchema");
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateJsonSchemaPath();
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
        testHelper.pause(1000);

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
        testHelper.pause(1000);
    }

    @Test
    @Order(20)
    void testGetJsonSchema() throws IOException {
        JsonSchema inputSchema = new TestDataGenerator().generateFunctionJsonSchema("randomInputSchema");
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindJsonSchemaPath().replace("{uid}", savedJsonInputSchemaUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(inputSchema.getName()));
    }

    @Test
    @Order(30)
    void testCreateWorkflow() throws IOException {
        Workflow workflow = new TestDataGenerator().generateWorkflow(savedJsonInputSchemaUid, savedJsonOutputSchemaUid,"workflow.json");
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateWorkflowPath();
        Workflow body = given()
                .body(workflow)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(workflow.getName()))
                .body("description", equalTo(workflow.getDescription()))
                .extract().body().as(Workflow.class);
        savedWorkflowUid = body.getUid();
        testHelper.pause(1000);
    }

    @Test
    @Order(40)
    void testGetWorkflow() throws IOException {
        Workflow workflow = new TestDataGenerator().generateWorkflow(savedJsonInputSchemaUid, savedJsonOutputSchemaUid,"workflow.json");
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindWorkflowPath().replace("{uid}", savedWorkflowUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(workflow.getName()));
    }


    @Test
    @Order(50)
    void testCreateSimpleWorkflowSteps() throws IOException {
        Function randomFunction = testHelper.findFunction(functionValueProvider, "function-random");
        Function sleepFunction = testHelper.findFunction(functionValueProvider, "function-sleep");
        Function fullcpuFunction = testHelper.findFunction(functionValueProvider, "function-fullcpu");
        List<SimpleWorkflowStep> workflowSteps = new TestDataGenerator().generateSimpleWorkflowSteps(savedWorkflowUid, randomFunction.getUid(), sleepFunction.getUid(),fullcpuFunction.getUid(),"workflow.json");
        for (SimpleWorkflowStep step : workflowSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateSimpleWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo(step.getName()));
            testHelper.pause(1000);
        }
    }

    @Test
    @Order(60)
    void testCreateSwitchWorkflowSteps() throws IOException {
        Function randomFunction = testHelper.findFunction(functionValueProvider, "function-random");
        Function sleepFunction = testHelper.findFunction(functionValueProvider, "function-sleep");
        Function fullcpuFunction = testHelper.findFunction(functionValueProvider, "function-fullcpu");
        List<SwitchWorkflowStep> workflowSteps = new TestDataGenerator().generateSwitchWorkflowSteps(savedWorkflowUid, randomFunction.getUid(), sleepFunction.getUid(),fullcpuFunction.getUid(),"workflow.json");
        for (SwitchWorkflowStep step : workflowSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateSwitchWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo(step.getName()));
            testHelper.pause(1000);
        }
    }

    @Test
    @Order(70)
    void testCreateWhileWorkflowSteps() throws IOException {
        Function randomFunction = testHelper.findFunction(functionValueProvider, "function-random");
        Function sleepFunction = testHelper.findFunction(functionValueProvider, "function-sleep");
        Function fullcpuFunction = testHelper.findFunction(functionValueProvider, "function-fullcpu");
        List<WhileWorkflowStep> workflowSteps = new TestDataGenerator().generateWhileWorkflowSteps(savedWorkflowUid, randomFunction.getUid(),sleepFunction.getUid(),fullcpuFunction.getUid(),"workflow.json");
        for (WhileWorkflowStep step : workflowSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateWhileWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo(step.getName()));
            testHelper.pause(1000);
        }
    }

    @Test
    @Order(80)
    void testCreateSubWorkflowSteps() throws IOException {
        testHelper.createWorkflow(workflowValueProvider, savedJsonInputSchemaUid, savedJsonOutputSchemaUid, "workflow2.json");
        Workflow subWorkflow = testHelper.findWorkflow(workflowValueProvider, "workflow2");
        testHelper.createWorkflowSteps(workflowValueProvider,functionValueProvider,subWorkflow.getUid(),"workflow2.json");
        List<SubWorkflowStep> workflowSteps = new TestDataGenerator().generateSubWorkflowSteps(savedWorkflowUid, subWorkflow.getUid(), "workflow.json");
        for (SubWorkflowStep step : workflowSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateSubWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo(step.getName()));
            testHelper.pause(1000);
        }
    }
}

package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.enums.RunStatus;
import de.lenneflow.lenneflowtests.model.JsonSchema;
import de.lenneflow.lenneflowtests.model.Workflow;
import de.lenneflow.lenneflowtests.model.WorkflowExecution;
import de.lenneflow.lenneflowtests.util.*;
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
@ContextConfiguration(classes = {WorkflowValueProvider.class, OrchestrationValueProvider.class, FunctionValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrchestrationBasicTests {

    static String savedWorkflowInstanceUid;

    final TestHelper testHelper = new TestHelper();


    @Autowired
    OrchestrationValueProvider orchestrationValueProvider;

    @Autowired
    WorkflowValueProvider workflowValueProvider;

    @Autowired
    FunctionValueProvider functionValueProvider;

    @BeforeAll
    void setUp() throws IOException {
        testHelper.deleteWorkflowStepsByWorkflowName(workflowValueProvider, "workflow3");
        testHelper.deleteWorkflowByName(workflowValueProvider, "workflow3");
        JsonSchema inputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomInputSchema");
        JsonSchema outputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomOutputSchema");
        Workflow workflow = testHelper.createWorkflow(workflowValueProvider, inputSchema.getUid(), outputSchema.getUid(), "workflow3.json");
        testHelper.createWorkflowSteps(workflowValueProvider, functionValueProvider, workflow.getUid(), "workflow3.json");
    }

    @Test
    @Order(10)
    void testStartWorkflowWithoutGlobalInput() {
        Workflow workflow = testHelper.findWorkflow(workflowValueProvider, "workflow1");
        String url = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getStartWorkflowPath().replace("{uid}", workflow.getUid());
        WorkflowExecution body = given()
                .get(url)
                .then()
                .statusCode(200)
                .body("workflowName", equalTo(workflow.getName()))
                .extract().body().as(WorkflowExecution.class);
        savedWorkflowInstanceUid = body.getRunUid();
        testHelper.pause(10000);

        String stateUrlWorkflow = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getWorkflowStatePath().replace("{uid}", savedWorkflowInstanceUid);
        given()
                .get(stateUrlWorkflow)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.COMPLETED.toString()));
    }


    @Test
    @Order(90)
    void testHorizontalPodScaling() throws IOException {
//        String url = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getStartWorkflowPath().replace("{uid}", workflow.getUid());
//        WorkflowExecution body = given()
//                .get(url)
//                .then()
//                .statusCode(200)
//                .body("runStatus", equalTo(RunStatus.RUNNING.toString()))
//                .extract().body().as(WorkflowExecution.class);
//        String runId = body.getRunUid();

        //Pods count checks

    }


}

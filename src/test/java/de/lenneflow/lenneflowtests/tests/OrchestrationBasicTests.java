package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.enums.RunStatus;
import de.lenneflow.lenneflowtests.model.Workflow;
import de.lenneflow.lenneflowtests.model.WorkflowExecution;
import de.lenneflow.lenneflowtests.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {WorkflowValueProvider.class, OrchestrationValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrchestrationBasicTests {

    static String savedWorkflowInstanceUid;

    final TestHelper testHelper = new TestHelper();


    @Autowired
    OrchestrationValueProvider orchestrationValueProvider;

    @Autowired
    WorkflowValueProvider workflowValueProvider;

    @BeforeAll
    void setUp() {

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
                .body("runStatus", equalTo(RunStatus.RUNNING.toString()))
                .extract().body().as(WorkflowExecution.class);
        savedWorkflowInstanceUid = body.getRunUid();
        Util.pause(5000);

        String stateUrlWorkflow = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getWorkflowStatePath().replace("{uid}", savedWorkflowInstanceUid);
        given()
                .get(stateUrlWorkflow)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.COMPLETED.toString()));

    }


}

package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.enums.RunStatus;
import de.lenneflow.lenneflowtests.model.Cluster;
import de.lenneflow.lenneflowtests.model.JsonSchema;
import de.lenneflow.lenneflowtests.model.Workflow;
import de.lenneflow.lenneflowtests.model.WorkflowExecution;
import de.lenneflow.lenneflowtests.util.*;
import io.fabric8.kubernetes.client.KubernetesClient;
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
@ContextConfiguration(classes = {WorkflowValueProvider.class, OrchestrationValueProvider.class, FunctionValueProvider.class, WorkerValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrchestrationBasicTests {

    static String savedWorkflowInstanceUid;

    final TestHelper testHelper = new TestHelper();

    Workflow hpaTestWorkflow;


    @Autowired
    OrchestrationValueProvider orchestrationValueProvider;

    @Autowired
    WorkflowValueProvider workflowValueProvider;

    @Autowired
    FunctionValueProvider functionValueProvider;

    @Autowired
    WorkerValueProvider workerValueProvider;

    @BeforeAll
    void setUp() throws IOException {
        hpaTestWorkflow = deleteAndCreateHPATestWorkflow();
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
        testHelper.pause(60000);

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
        String url = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getStartWorkflowPath().replace("{uid}", hpaTestWorkflow.getUid());
        WorkflowExecution body = given()
                .get(url)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.RUNNING.toString()))
                .extract().body().as(WorkflowExecution.class);
        String runId = body.getRunUid();
        testHelper.pause(30000);
        Cluster cluster = testHelper.findCluster(workerValueProvider, "MyLocalCluster");
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster);
        Integer replicaCount = k8sClient.apps().deployments().inNamespace("lenneflow").withName("function-fullcpu").get().getStatus().getReplicas();
        Assertions.assertTrue(replicaCount > 2);
        testHelper.pause(100000);
        String checkStateUrl = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getWorkflowStatePath().replace("{uid}", runId);
        given()
                .get(checkStateUrl)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.COMPLETED.toString()));
    }

    private Workflow deleteAndCreateHPATestWorkflow() throws IOException {
        testHelper.deleteWorkflowStepsByWorkflowName(workflowValueProvider, "workflow3");
        testHelper.deleteWorkflowByName(workflowValueProvider, "workflow3");
        JsonSchema inputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomInputSchema");
        JsonSchema outputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomOutputSchema");
        Workflow workflow = testHelper.createWorkflow(workflowValueProvider, inputSchema.getUid(), outputSchema.getUid(), "workflow3.json");
        testHelper.createWorkflowSteps(workflowValueProvider, functionValueProvider, workflow.getUid(), "workflow3.json");
        return workflow;
    }


}

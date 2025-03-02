package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.enums.RunStatus;
import de.lenneflow.lenneflowtests.model.*;
import de.lenneflow.lenneflowtests.util.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {WorkflowValueProvider.class, OrchestrationValueProvider.class, FunctionValueProvider.class, WorkerValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrchestrationBasicTests {

    static String savedWorkflowInstanceUid;

    static String savedHPAByRequestWorkflowRunId;

    static String savedHPAByCpuWorkflowRunId;

    static String clusterName = "TestAWSCLuster";

    final TestHelper testHelper = new TestHelper();


    @Autowired
    OrchestrationValueProvider orchestrationValueProvider;

    @Autowired
    WorkflowValueProvider workflowValueProvider;

    @Autowired
    FunctionValueProvider functionValueProvider;

    @Autowired
    WorkerValueProvider workerValueProvider;

    @BeforeAll
    void setUp(){
        testHelper.deleteAllWorkflowInstances(orchestrationValueProvider);
    }

    @Test
    @Order(10)
    void testStartWorkflowWithInvalidInputData() {
        Workflow workflow = testHelper.findWorkflow(workflowValueProvider, "workflow1");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("Invalid", "Data");
        String url = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getStartWorkflowPath().replace("{uid}", workflow.getUid());
        given()
                .body(inputData)
                .contentType("application/json")
                .get(url)
                .then()
                .statusCode(500); //TODO
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
        testHelper.pause(90000);

        String stateUrlWorkflow = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getWorkflowStatePath().replace("{uid}", savedWorkflowInstanceUid);
        given()
                .get(stateUrlWorkflow)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.COMPLETED.toString()));
    }

    @Test
    @Order(50)
    void testHPAByRequestsUpScaling() throws IOException {
        Workflow hpaTestWorkflow = deleteAndCreateRequestsHPATestWorkflow();
        String url = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getStartWorkflowPath().replace("{uid}", hpaTestWorkflow.getUid());
        WorkflowExecution body = given()
                .get(url)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.RUNNING.toString()))
                .extract().body().as(WorkflowExecution.class);
        savedHPAByRequestWorkflowRunId = body.getRunUid();
        testHelper.pause(60000);
        Cluster cluster = testHelper.findCluster(workerValueProvider, clusterName);
        AccessToken token = testHelper.extractAccessTokenObject(workerValueProvider, cluster.getUid());
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster, token);
        Integer replicaCount = k8sClient.apps().deployments().inNamespace("lenneflow").withName("function-random").get().getStatus().getReplicas();
        Assertions.assertTrue(replicaCount > 2);

    }

    @Test
    @Order(50)
    void testHPAByRequestsDownScaling() throws IOException {
        testHelper.pause(100000);//TODO
        String checkStateUrl = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getWorkflowStatePath().replace("{uid}", savedHPAByRequestWorkflowRunId);
        given()
                .get(checkStateUrl)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.COMPLETED.toString()));
        Cluster cluster = testHelper.findCluster(workerValueProvider, clusterName);
        AccessToken token = testHelper.extractAccessTokenObject(workerValueProvider, cluster.getUid());
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster, token);
        Integer replicaCount = k8sClient.apps().deployments().inNamespace("lenneflow").withName("function-random").get().getStatus().getReplicas();
        Assertions.assertEquals(1, (int) replicaCount);
    }

    @Test
    @Order(60)
    void testHPAByCpuUpScaling() throws IOException {
        Workflow hpaTestWorkflow = deleteAndCreateCpuHPATestWorkflow();
        String url = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getStartWorkflowPath().replace("{uid}", hpaTestWorkflow.getUid());
        WorkflowExecution body = given()
                .get(url)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.RUNNING.toString()))
                .extract().body().as(WorkflowExecution.class);
        savedHPAByCpuWorkflowRunId = body.getRunUid();
        testHelper.pause(420, TimeUnit.SECONDS);//TODO
        Cluster cluster = testHelper.findCluster(workerValueProvider, clusterName);
        AccessToken token = testHelper.extractAccessTokenObject(workerValueProvider, cluster.getUid());
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster, token);
        Integer replicaCount = k8sClient.apps().deployments().inNamespace("lenneflow").withName("function-fullcpu").get().getStatus().getReplicas();
        Assertions.assertTrue(replicaCount > 2);
    }

    @Test
    @Order(60)
    void testVerticalNodesUpScaling() throws IOException {
        testHelper.pause(100000); //TODO
        Cluster cluster = testHelper.findCluster(workerValueProvider, clusterName);
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster);
        int nodeCount = k8sClient.nodes().list().getItems().size();
        Assertions.assertTrue(nodeCount >= 2);

    }

    @Test
    @Order(70)
    void testHPAByCpuDownScaling() throws IOException {
        testHelper.pause(100000);//TODO
        String checkStateUrl = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getWorkflowStatePath().replace("{uid}", savedHPAByCpuWorkflowRunId);
        given()
                .get(checkStateUrl)
                .then()
                .statusCode(200)
                .body("runStatus", equalTo(RunStatus.COMPLETED.toString()));
        Cluster cluster = testHelper.findCluster(workerValueProvider, clusterName);
        AccessToken token = testHelper.extractAccessTokenObject(workerValueProvider, cluster.getUid());
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster, token);
        Integer replicaCount = k8sClient.apps().deployments().inNamespace("lenneflow").withName("function-fullcpu").get().getStatus().getReplicas();
        Assertions.assertEquals(1, (int) replicaCount);
    }

    @Test
    @Order(60)
    void testVerticalNodesDownScaling() throws IOException {
        testHelper.pause(100000);//TODO
        Cluster cluster = testHelper.findCluster(workerValueProvider, clusterName);
        AccessToken token = testHelper.extractAccessTokenObject(workerValueProvider, cluster.getUid());
        KubernetesClient k8sClient = KubernetesUtil.getKubernetesClient(cluster, token);
        int nodeCount = k8sClient.nodes().list().getItems().size();
        Assertions.assertEquals(1, nodeCount);
    }

    private Workflow deleteAndCreateRequestsHPATestWorkflow() throws IOException {
        testHelper.deleteWorkflowStepsByWorkflowName(workflowValueProvider, "workflow4");
        testHelper.deleteWorkflowByName(workflowValueProvider, "workflow4");
        JsonSchema inputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomInputSchema");
        JsonSchema outputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomOutputSchema");
        Workflow workflow = testHelper.createWorkflow(workflowValueProvider, inputSchema.getUid(), outputSchema.getUid(), "workflow4.json");
        testHelper.createWorkflowSteps(workflowValueProvider, functionValueProvider, workflow.getUid(), "workflow4.json");
        return workflow;
    }

    private Workflow deleteAndCreateCpuHPATestWorkflow() throws IOException {
        testHelper.deleteWorkflowStepsByWorkflowName(workflowValueProvider, "workflow3");
        testHelper.deleteWorkflowByName(workflowValueProvider, "workflow3");
        JsonSchema inputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomInputSchema");
        JsonSchema outputSchema = testHelper.createWorkflowJsonSchema(workflowValueProvider, "randomOutputSchema");
        Workflow workflow = testHelper.createWorkflow(workflowValueProvider, inputSchema.getUid(), outputSchema.getUid(), "workflow3.json");
        testHelper.createWorkflowSteps(workflowValueProvider, functionValueProvider, workflow.getUid(), "workflow3.json");
        return workflow;
    }


}

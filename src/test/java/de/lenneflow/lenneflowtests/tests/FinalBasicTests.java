package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.enums.DeploymentState;
import de.lenneflow.lenneflowtests.model.Cluster;
import de.lenneflow.lenneflowtests.model.Function;
import de.lenneflow.lenneflowtests.util.FunctionValueProvider;
import de.lenneflow.lenneflowtests.util.TestHelper;
import de.lenneflow.lenneflowtests.util.WorkerValueProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {FunctionValueProvider.class, WorkerValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinalBasicTests {

    @Autowired
    FunctionValueProvider functionValueProvider;

    @Autowired
    WorkerValueProvider workerValueProvider;

    final TestHelper testHelper = new TestHelper();


    @Test
    @Order(10)
    void testUndeployFunctions() {
        List<Function> deployedFunctions = testHelper.findDeployedFunctions(functionValueProvider);
        for (Function function : deployedFunctions) {
            System.out.println("Start undeploying " + function.getName());
            String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getUnDeployFunctionPath().replace("{uid}", function.getUid());
            given()
                    .when()
                    .get(url)
                    .then()
                    .statusCode(200);
        }
        testHelper.pause(10000);

        for (Function function : deployedFunctions) {
            String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindFunctionPath().replace("{uid}", function.getUid());
            given()
                    .when()
                    .get(url)
                    .then()
                    .statusCode(200)
                    .body("deploymentState", equalTo(DeploymentState.UNDEPLOYED.toString()));
        }
    }

    @Test
    @Order(20)
    void testDeleteClustersInCloud() {
        List<Cluster> clusters = testHelper.findManagedClusters(workerValueProvider);
        for (Cluster cluster : clusters) {
            System.out.println("Start deleting " + cluster.getClusterName());
            String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getDeleteClusterPath().replace("{uid}", cluster.getUid());
            given()
                    .when()
                    .delete(url)
                    .then()
                    .statusCode(200);
        }
        testHelper.pause(15, TimeUnit.MINUTES);
        for (Cluster cluster : clusters) {
            String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getDeleteClusterPath().replace("{uid}", cluster.getUid());
            given()
                    .when()
                    .get(url)
                    .then()
                    .statusCode(404);
        }
    }

}

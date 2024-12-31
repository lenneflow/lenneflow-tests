package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.model.CloudCredential;
import de.lenneflow.lenneflowtests.model.Cluster;
import de.lenneflow.lenneflowtests.model.ManagedCluster;
import de.lenneflow.lenneflowtests.util.TestDataGenerator;
import de.lenneflow.lenneflowtests.util.TestHelper;
import de.lenneflow.lenneflowtests.util.WorkerValueProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {WorkerValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagedClusterBasicTests {

    static String savedCredentialUid;
    static String savedClusterUid;

    final TestHelper testHelper = new TestHelper();


    @Autowired
    WorkerValueProvider workerValueProvider;

    @BeforeAll
    void setUp() {
        testHelper.deleteAllCredentialsTables(workerValueProvider);
    }

    @Test
    @Order(10)
    void testCreateCloudCredential() throws IOException {
        CloudCredential credential = new TestDataGenerator().generateCloudCredentialObject();
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getCreateCloudCredentialPath();
        CloudCredential body = given()
                .body(credential)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(credential.getName()))
                .extract().body().as(CloudCredential.class);
        System.out.println(body.getUid());
        savedCredentialUid = body.getUid();
        testHelper.pause(1000);
    }

    @Test
    @Order(20)
    void testGetCloudCredential() throws IOException {
        CloudCredential credential = new TestDataGenerator().generateCloudCredentialObject();
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindCloudCredentialPath().replace("{uid}", savedCredentialUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("name", equalTo(credential.getName()));
    }

    @Test
    @Order(30)
    void testCreateCloudCluster() throws IOException {
        ManagedCluster cluster = new TestDataGenerator().generateCloudClusterObject(savedCredentialUid);
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getCreateCloudClusterPath();
        Cluster body = given()
                .body(cluster)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("clusterName", equalTo(cluster.getClusterName()))
                .extract().body().as(Cluster.class);
        savedClusterUid = body.getUid();
        testHelper.pause(15, TimeUnit.MINUTES);
    }

    @Test
    @Order(40)
    void testCheckCloudClusterCreated() throws IOException {
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindClusterPath().replace("{uid}", savedClusterUid);
        ManagedCluster cluster = new TestDataGenerator().generateCloudClusterObject(savedCredentialUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("clusterName", equalTo(cluster.getClusterName()));
    }

}

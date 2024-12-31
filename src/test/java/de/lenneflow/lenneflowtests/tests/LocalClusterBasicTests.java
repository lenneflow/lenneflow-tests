package de.lenneflow.lenneflowtests.tests;

import de.lenneflow.lenneflowtests.util.TestDataGenerator;
import de.lenneflow.lenneflowtests.util.TestHelper;
import de.lenneflow.lenneflowtests.util.WorkerValueProvider;
import de.lenneflow.lenneflowtests.model.AccessToken;
import de.lenneflow.lenneflowtests.model.LocalCluster;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
@ContextConfiguration(classes = {WorkerValueProvider.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalClusterBasicTests {

    static String savedTokenUid;
    static String savedClusterUid;

    final TestHelper testHelper = new TestHelper();


    @Autowired
    WorkerValueProvider workerValueProvider;

    @BeforeAll
    void setUp() {
        testHelper.deleteAllAccessTokenTables(workerValueProvider);
        testHelper.deleteAllUnmanagedClustersTables(workerValueProvider);
    }

    @Test
    @Order(10)
    void testCreateLocalAccessToken() throws IOException {
        AccessToken token = new TestDataGenerator().generateAccessTokenObject();
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getCreateAccessTokenPath();
        AccessToken body = given()
                .body(token)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("description", equalTo(token.getDescription()))
                .extract().body().as(AccessToken.class);
        System.out.println(body.getUid());
        savedTokenUid = body.getUid();
        testHelper.pause(1000);
    }

    @Test
    @Order(20)
    void testGetLocalAccessToken() throws IOException {
        AccessToken token = new TestDataGenerator().generateAccessTokenObject();
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAccessTokenPath().replace("{uid}", savedTokenUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("description", equalTo(token.getDescription()));
    }

    @Test
    @Order(30)
    void testRegisterLocalCluster() throws IOException {
        LocalCluster cluster = new TestDataGenerator().generateLocalClusterObject(savedTokenUid, workerValueProvider.getLocalApiServerEndpointPath(), workerValueProvider.getLocalHostUrlPath());
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getRegisterLocalClusterPath();
        LocalCluster body = given()
                .body(cluster)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .body("hostUrl", equalTo(cluster.getHostUrl()))
                .extract().body().as(LocalCluster.class);
        savedClusterUid = body.getUid();
        testHelper.pause(1000);
    }

    @Test
    @Order(40)
    void testGetLocalCluster() throws IOException {
        LocalCluster cluster = new TestDataGenerator().generateLocalClusterObject(savedTokenUid, workerValueProvider.getLocalApiServerEndpointPath(), workerValueProvider.getLocalHostUrlPath());
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindClusterPath().replace("{uid}", savedClusterUid);
        given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("clusterName", equalTo(cluster.getClusterName()));
    }
}

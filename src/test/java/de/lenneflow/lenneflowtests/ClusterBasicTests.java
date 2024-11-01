package de.lenneflow.lenneflowtests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
class ClusterBasicTests {

    @Value("${lenneflow.root.url}")
    String rootUrl;

    @Test
    void testRegisterLocalCluster() {
        RestAssured.given()
                .when()
                .get(rootUrl)
                .then()
                .statusCode(200);
    }
}

package de.lenneflow.lenneflowtests.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.TestPropertySource;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TestComponent
@TestPropertySource("classpath:application.properties")
public class WorkerValueProvider {

    @Value("${lenneflow.root.url}")
    private String rootUrl;

    @Value("${lenneflow.worker.root.url}")
    private String workerRootUrl;

    @Value("${lenneflow.local-cluster.api-server-endpoint}")
    private String localApiServerEndpointPath;

    @Value("${lenneflow.local-cluster.host-url}")
    private String localHostUrlPath;

    @Value("${resource.access-token.create}")
    private String createAccessTokenPath;

    @Value("${resource.cluster.register}")
    private String registerLocalClusterPath;

    @Value("${resource.cluster.find}")
    private String findClusterPath;

    @Value("${resource.cluster.delete}")
    private String deleteClusterPath;

    @Value("${resource.cluster.find-all}")
    private String findAllClustersPath;

    @Value("${resource.access-token.find}")
    private String findAccessTokenPath;

    @Value("${resource.access-token.delete}")
    private String deleteAccessTokenPath;

    @Value("${resource.access-token.find-all}")
    private String findAllAccessTokenPath;


}

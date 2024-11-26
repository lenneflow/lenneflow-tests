package de.lenneflow.lenneflowtests.util;

import de.lenneflow.lenneflowtests.model.AccessToken;
import de.lenneflow.lenneflowtests.model.Cluster;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import java.io.IOException;

public class KubernetesUtil {


    public static KubernetesClient getKubernetesClient(Cluster kubernetesCluster) throws IOException {
        AccessToken token = new TestDataGenerator().generateAccessTokenObject();
        String  masterUrl = kubernetesCluster.getApiServerEndpoint();

        Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withTrustCerts(true)
                .withOauthToken(token.getToken())
                .build();
        return new KubernetesClientBuilder().withConfig(config).build();
    }
}

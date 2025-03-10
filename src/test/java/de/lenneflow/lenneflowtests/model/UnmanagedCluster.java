package de.lenneflow.lenneflowtests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.lenneflow.lenneflowtests.enums.CloudProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnmanagedCluster {

    private String clusterName;

    private String region;

    private String description;

    private List<String> supportedFunctionTypes = new ArrayList<>();

    private String apiServerEndpoint;

    private String caCertificate;

    private CloudProvider cloudProvider;

    private String cloudCredentialUid;

}

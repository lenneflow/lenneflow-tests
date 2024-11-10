package de.lenneflow.lenneflowtests.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.lenneflow.lenneflowtests.enums.DeploymentState;
import de.lenneflow.lenneflowtests.enums.PackageRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for the entity function
 * @author Idrissa Ganemtore
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Function {

    private String uid;

    private String name;

    private String description;

    private String type;

    private DeploymentState deploymentState;

    private PackageRepository packageRepository;

    private String resourcePath;

    private int servicePort;

    private boolean lazyDeployment;

    private String imageName;

    private String cpuRequest;

    private String memoryRequest;

    private String inputSchemaUid;

    private String outputSchemaUid;

}


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
public class OrchestrationValueProvider {

    @Value("${lenneflow.root.url}")
    private String rootUrl;

    @Value("${lenneflow.orchestration.root.url}")
    private String orchestrationRootUrl;

    @Value("${resource.orchestration.workflow.start}")
    private String startWorkflowPath;

    @Value("${resource.orchestration.workflow.with-input-id.start}")
    private String startWorkflowWithInputIdPath;

    @Value("${resource.orchestration.workflow.with-input-payload.start}")
    private String startWorkflowWithInputPayloadPath;

    @Value("${resource.orchestration.workflow.stop}")
    private String stopWorkflowPath;

    @Value("${resource.orchestration.workflow.resume}")
    private String resumeWorkflowPath;

    @Value("${resource.orchestration.workflow.pause}")
    private String pauseWorkflowPath;

    @Value("${resource.orchestration.workflow.state}")
    private String workflowStatePath;

}

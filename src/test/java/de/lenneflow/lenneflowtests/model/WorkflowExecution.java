package de.lenneflow.lenneflowtests.model;

import de.lenneflow.lenneflowtests.enums.RunStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB entity for Workflow execution
 *
 * @author Idrissa Ganemtore
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecution {

    //the run UID is the workflow Instance UID
    private String runUid;

    private String workflowName;

    private String workflowDescription;

    private RunStatus runStatus;

    private List<WorkflowStepInstance> runSteps;

    private int workflowVersion;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String failureReason;

    private Map<String, Object> outputData = new HashMap<>();

}

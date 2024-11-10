package de.lenneflow.lenneflowtests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhileWorkflowStep {

    private String uid;

    private String name;

    private String workflowUid;

    private String description;

    private int executionOrder;

    private Integer retryCount = 0;

    private String functionUid;

    private String stopCondition;

    private Map<String, Object> inputData = new LinkedHashMap<>();

}

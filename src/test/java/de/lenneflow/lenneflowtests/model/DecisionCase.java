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
public class DecisionCase {

    private String name;

    private String functionUid;

    private String subWorkflowUid;

    private Map<String, Object> inputData = new LinkedHashMap<>();

    private Integer retryCount;
}

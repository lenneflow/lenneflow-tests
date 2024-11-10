package de.lenneflow.lenneflowtests.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.lenneflow.lenneflowtests.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    static ObjectMapper mapper = new ObjectMapper();
    PropertiesReader reader = new PropertiesReader();

    public AccessToken generateAccessTokenObject() throws IOException {
        AccessToken accessToken = mapper.readValue(getClass().getResource("/test-data/access-tokens.json"), AccessToken.class);
        String token = reader.getProperty("clusterApiToken");
        accessToken.setToken(token);
        return accessToken;
    }

    public JsonSchema generateFunctionJsonSchema(String schemaName) throws IOException {
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/schema.json"));
        return  mapper.readValue(node.get(schemaName).toString(), JsonSchema.class);
    }

    public Function generateFunction(String jsonObjectName, String inputSchemaUid, String outputSchemaUid, boolean lazyDeployment) throws IOException {
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/functions.json"));
        Function function =  mapper.readValue(node.get(jsonObjectName).toString(), Function.class);
        function.setInputSchemaUid(inputSchemaUid);
        function.setLazyDeployment(lazyDeployment);
        function.setOutputSchemaUid(outputSchemaUid);
        return function;
    }

    public LocalCluster generateLocalClusterObject(String tokenUid, String apiServerEndpoint, String hostUrl) throws IOException {
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/clusters.json"));
        LocalCluster localCluster = mapper.readValue(node.get("LOCAL").toString(), LocalCluster.class);
        System.out.println(localCluster.getClusterName());
        localCluster.setApiServerEndpoint(apiServerEndpoint);
        localCluster.setKubernetesAccessTokenUid(tokenUid);
        localCluster.setHostUrl(hostUrl);
        return localCluster;
    }

    public Workflow generateWorkflow(String inputSchemaUid, String outputSchemaUid) throws IOException {
        Workflow workflow = mapper.readValue(getClass().getResource("/test-data/workflow.json"), Workflow.class);
        workflow.setInputDataSchemaUid(inputSchemaUid);
        workflow.setOutputDataSchemaUid(outputSchemaUid);
        return workflow;
    }

    public List<SimpleWorkflowStep> generateSimpleWorkflowSteps(String workflowUid, String randomFunctionId, String sleepFunctionId) throws IOException {
        List<SimpleWorkflowStep> simpleWorkflowSteps = new ArrayList<>();
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/workflow-steps.json"));
        SimpleWorkflowStep[] steps = mapper.readValue(node.get("simple").toString(), SimpleWorkflowStep[].class);
        for(SimpleWorkflowStep step : steps) {
            step.setFunctionUid(step.getFunctionUid().replace("randomFunction", randomFunctionId).replace("sleepFunction", sleepFunctionId));
            step.setWorkflowUid(workflowUid);
            simpleWorkflowSteps.add(step);
        }
        return simpleWorkflowSteps;
    }

    public List<WhileWorkflowStep> generateWhileWorkflowSteps(String workflowUid, String randomFunctionId, String sleepFunctionId) throws IOException {
        List<WhileWorkflowStep> workflowSteps = new ArrayList<>();
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/workflow-steps.json"));
        WhileWorkflowStep[] steps = mapper.readValue(node.get("while").toString(), WhileWorkflowStep[].class);
        for(WhileWorkflowStep step : steps) {
            step.setFunctionUid(step.getFunctionUid().replace("randomFunction", randomFunctionId).replace("sleepFunction", sleepFunctionId));
            step.setWorkflowUid(workflowUid);
            workflowSteps.add(step);
        }
        return workflowSteps;
    }

    public List<SubWorkflowStep> generateSubWorkflowSteps(String workflowUid, String subWorkflowUid) throws IOException {
        List<SubWorkflowStep> workflowSteps = new ArrayList<>();
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/workflow-steps.json"));
        SubWorkflowStep[] steps = mapper.readValue(node.get("while").toString(), SubWorkflowStep[].class);
        for(SubWorkflowStep step : steps) {
            step.setSubWorkflowUid(subWorkflowUid);
            step.setWorkflowUid(workflowUid);
            workflowSteps.add(step);
        }
        return workflowSteps;
    }

    public List<SwitchWorkflowStep> generateSwitchWorkflowSteps(String workflowUid, String randomFunctionId, String sleepFunctionId) throws IOException {
        List<SwitchWorkflowStep> workflowSteps = new ArrayList<>();
        JsonNode node = mapper.readTree(getClass().getResource("/test-data/workflow-steps.json"));
        SwitchWorkflowStep[] steps = mapper.readValue(node.get("switch").toString(), SwitchWorkflowStep[].class);
        for(SwitchWorkflowStep step : steps) {
            for (DecisionCase decisionCase : step.getDecisionCases()) {
                decisionCase.setFunctionUid(decisionCase.getFunctionUid().replace("randomFunction", randomFunctionId).replace("sleepFunction", sleepFunctionId));
            }
            step.setWorkflowUid(workflowUid);
            workflowSteps.add(step);
        }
        return workflowSteps;
    }

}
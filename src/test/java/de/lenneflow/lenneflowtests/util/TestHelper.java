package de.lenneflow.lenneflowtests.util;

import de.lenneflow.lenneflowtests.enums.DeploymentState;
import de.lenneflow.lenneflowtests.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;


public class TestHelper {


    // Delete Section

    public void deleteAllUnmanagedClustersTables(WorkerValueProvider workerValueProvider) {
        String findListUrl = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAllClustersPath();
        String deleteUrl = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getDeleteClusterPath();
        Cluster[] clusters = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(Cluster[].class);
        for (Cluster cluster : clusters) {
            if(!cluster.isManaged()){
                given()
                        .when()
                        .delete(deleteUrl.replace("{uid}", cluster.getUid()));
            }
        }
    }

    public void deleteAllAccessTokenTables(WorkerValueProvider workerValueProvider) {
        String findListUrl = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAllAccessTokenPath();
        String deleteUrl = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getDeleteAccessTokenPath();
        AccessToken[] tokens = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(AccessToken[].class);
        for (AccessToken token : tokens) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", token.getUid()));
        }
    }

    public void deleteAllCredentialsTables(WorkerValueProvider workerValueProvider) {
        String findListUrl = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAllCloudCredentialPath();
        String deleteUrl = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getDeleteCloudCredentialPath();
        CloudCredential[] credentials = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(CloudCredential[].class);
        for (CloudCredential credential : credentials) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", credential.getUid()));
        }
    }

    public void deleteAllFunctionsTables(FunctionValueProvider functionValueProvider) {
        String findListUrl = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindAllFunctionsList();
        String deleteUrl = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getDeleteFunctionPath();
        Function[] functionList = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(Function[].class);
        for (Function function : functionList) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", function.getUid()));
        }
    }

    public void deleteAllFunctionsJsonSchema(FunctionValueProvider functionValueProvider) {
        String findListUrl = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindJsonSchemaListPath();
        String deleteUrl = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getDeleteJsonSchemaPath();
        JsonSchema[] schemaList = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(JsonSchema[].class);
        for (JsonSchema schema : schemaList) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", schema.getUid()));
        }
    }

    public void deleteAllWorkflowsJsonSchema(WorkflowValueProvider workflowValueProvider) {
        String findListUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindJsonSchemaListPath();
        String deleteUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getDeleteJsonSchemaPath();
        JsonSchema[] schemaList = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(JsonSchema[].class);
        for (JsonSchema schema : schemaList) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", schema.getUid()));
        }
    }

    public void deleteAllWorkflowSteps(WorkflowValueProvider workflowValueProvider) {
        String findListUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindAllWorkflowStepsPath();
        String deleteUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getDeleteWorkflowStepPath();
        WorkflowStep[] stepList = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(WorkflowStep[].class);
        for (WorkflowStep step : stepList) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", step.getUid()));
        }
    }

    public void deleteAllWorkflowInstances(OrchestrationValueProvider orchestrationValueProvider) {
        String findListUrl = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getFindAllWorkflowInstancesPath();
        System.out.println(findListUrl);
        String deleteUrl = orchestrationValueProvider.getOrchestrationRootUrl() + orchestrationValueProvider.getDeleteWorkflowInstancesPath();
        WorkflowExecution[] instances = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(WorkflowExecution[].class);
        for (WorkflowExecution instance : instances) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", instance.getRunUid()));
        }
    }

    public void deleteAllWorkflows(WorkflowValueProvider workflowValueProvider) {
        String findListUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindAllWorkflowsPath();
        String deleteUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getDeleteWorkflowPath();
        Workflow[] workflows = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(Workflow[].class);
        for (Workflow workflow : workflows) {
            given()
                    .when()
                    .delete(deleteUrl.replace("{uid}", workflow.getUid()));
        }
    }

    public void deleteWorkflowByName(WorkflowValueProvider workflowValueProvider, String workflowName) {
        String findListUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindAllWorkflowsPath();
        String deleteUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getDeleteWorkflowPath();
        Workflow[] workflows = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(Workflow[].class);
        for (Workflow workflow : workflows) {
            if(workflow.getName().equalsIgnoreCase(workflowName)) {
                given()
                        .when()
                        .delete(deleteUrl.replace("{uid}", workflow.getUid()));
            }
        }
    }

    public void deleteWorkflowStepsByWorkflowName(WorkflowValueProvider workflowValueProvider, String workflowName){
        String findListUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindAllWorkflowsPath();
        String findStepListUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindAllWorkflowStepsPath();
        String deleteStepUrl = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getDeleteWorkflowStepPath();
        Workflow[] workflows = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(Workflow[].class);
        for (Workflow workflow : workflows) {
            if(workflow.getName().equalsIgnoreCase(workflowName)) {
                WorkflowStep[] steps = given()
                        .when()
                        .get(findStepListUrl)
                        .then()
                        .extract().body().as(WorkflowStep[].class);
                for (WorkflowStep step : steps) {
                    if(step.getWorkflowUid().equalsIgnoreCase(workflow.getUid())) {
                        given()
                                .when()
                                .delete(deleteStepUrl.replace("{uid}", step.getUid()));
                    }
                }
            }
        }
    }


    // Create Section

    public Workflow createWorkflow(WorkflowValueProvider workflowValueProvider, String inputSchemaUid, String outputSchemaUid, String jsonFileName) throws IOException {
        Workflow workflow = new TestDataGenerator().generateWorkflow(inputSchemaUid, outputSchemaUid, jsonFileName);
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateWorkflowPath();
        return given()
                .body(workflow)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .statusCode(200)
                .extract().body().as(Workflow.class);
    }

    public void createWorkflowSteps(WorkflowValueProvider workflowValueProvider, FunctionValueProvider functionValueProvider, String workflowUid, String jsonFileName) throws IOException {
        Function randomFunction = findFunction(functionValueProvider, "function-random");
        Function sleepFunction = findFunction(functionValueProvider, "function-sleep");
        Function fullcpuFunction = findFunction(functionValueProvider, "function-fullcpu");
        //Simple Steps
        List<SimpleWorkflowStep> simpleSteps = new TestDataGenerator().generateSimpleWorkflowSteps(workflowUid, randomFunction.getUid(), sleepFunction.getUid(),fullcpuFunction.getUid(), jsonFileName);
        for (SimpleWorkflowStep step : simpleSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateSimpleWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200);
            pause(1000);
        }
        //Switch steps
        List<SwitchWorkflowStep> switchSteps = new TestDataGenerator().generateSwitchWorkflowSteps(workflowUid, randomFunction.getUid(), sleepFunction.getUid(),fullcpuFunction.getUid(), jsonFileName);
        for (SwitchWorkflowStep step : switchSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateSwitchWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200);
            pause(1000);
        }
        //While Steps
        List<WhileWorkflowStep> whileSteps = new TestDataGenerator().generateWhileWorkflowSteps(workflowUid, randomFunction.getUid(), sleepFunction.getUid(),fullcpuFunction.getUid(), jsonFileName);
        for (WhileWorkflowStep step : whileSteps) {
            String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateWhileWorkflowStepPath();
            given()
                    .body(step)
                    .contentType("application/json")
                    .when()
                    .post(url)
                    .then()
                    .statusCode(200);
            pause(1000);
        }
    }


    public JsonSchema createFunctionJsonSchema(FunctionValueProvider functionValueProvider, String schemaName) throws IOException {
        JsonSchema inputSchema = new TestDataGenerator().generateFunctionJsonSchema(schemaName);
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getCreateJsonSchemaPath();
        return given()
                .body(inputSchema)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .extract().body().as(JsonSchema.class);
    }

    public JsonSchema createWorkflowJsonSchema(WorkflowValueProvider workflowValueProvider, String schemaName) throws IOException {
        JsonSchema inputSchema = new TestDataGenerator().generateFunctionJsonSchema(schemaName);
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getCreateJsonSchemaPath();
        return given()
                .body(inputSchema)
                .contentType("application/json")
                .when()
                .post(url)
                .then()
                .extract().body().as(JsonSchema.class);
    }



    // Find Section

    public List<Function> findDeployedFunctions(FunctionValueProvider functionValueProvider) {
        List<Function> result = new ArrayList<>();
        String findListUrl = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindAllFunctionsList();
        Function[] functionList = given()
                .when()
                .get(findListUrl)
                .then()
                .extract().body().as(Function[].class);
        for (Function function : functionList) {
            if(function.getDeploymentState() == DeploymentState.DEPLOYED){
                result.add(function);
            }
        }
        return result;
    }

    public Function findFunction(FunctionValueProvider functionValueProvider, String functionName) {
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindAllFunctionsList();
        Function[] functions = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Function[].class);
        for (Function function : functions) {
            if (function.getName().equalsIgnoreCase(functionName))
                return function;
        }
        return null;
    }

    public Workflow findWorkflow(WorkflowValueProvider workflowValueProvider, String workflowName) {
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindAllWorkflowsPath();
        Workflow[] workflows = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Workflow[].class);
        for (Workflow workflow : workflows) {
            if (workflow.getName().equalsIgnoreCase(workflowName))
                return workflow;
        }
        return null;
    }

    public List<Cluster> findAllClusters(WorkerValueProvider workerValueProvider) {
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAllClustersPath();
        Cluster[] clusters = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Cluster[].class);
        return Arrays.stream(clusters).toList();
    }

    public List<Cluster> findManagedClusters(WorkerValueProvider workerValueProvider) {
        List<Cluster> clusterList = new ArrayList<>();
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAllClustersPath();
        Cluster[] clusters = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Cluster[].class);
        for (Cluster cluster : clusters) {
            if(cluster.isManaged()){
                clusterList.add(cluster);
            }
        }
        return clusterList;
    }

    public Cluster findCluster(WorkerValueProvider workerValueProvider, String clusterName) {
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getFindAllClustersPath();
        Cluster[] clusters = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Cluster[].class);
        for (Cluster cluster : clusters) {
            if (cluster.getClusterName().equalsIgnoreCase(clusterName))
                return cluster;
        }
        return null;
    }

    public AccessToken extractAccessTokenObject(WorkerValueProvider workerValueProvider, String clusterUid) {
        String url = workerValueProvider.getWorkerRootUrl() + workerValueProvider.getExtractAccessTokenPath().replace("{uid}", clusterUid);
        return given()
                .when()
                .get(url)
                .then()
                .extract().body().as(AccessToken.class);
    }

    public void pause(int millis) {
        pause(millis, TimeUnit.MILLISECONDS);
    }

    public void pause(int time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

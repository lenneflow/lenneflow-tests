package de.lenneflow.lenneflowtests.util;

import de.lenneflow.lenneflowtests.model.Function;
import de.lenneflow.lenneflowtests.model.JsonSchema;
import de.lenneflow.lenneflowtests.model.Workflow;

import java.io.IOException;

import static io.restassured.RestAssured.given;


public class TestHelper {


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
        String findListUrl = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindJsonSchemaList();
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

    public JsonSchema createJsonSchema(FunctionValueProvider functionValueProvider, String schemaName) throws IOException {
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

    public Function findFunction(FunctionValueProvider functionValueProvider, String functionName) {
        String url = functionValueProvider.getFunctionRootUrl() + functionValueProvider.getFindAllFunctionsList();
        Function[] functions = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Function[].class);
        for (Function function : functions) {
            if(function.getName().equalsIgnoreCase(functionName))
                return function;
        }
        return null;
    }

    public Workflow findWorkflow(WorkflowValueProvider workflowValueProvider, String workflowName) {
        String url = workflowValueProvider.getWorkflowRootUrl() + workflowValueProvider.getFindWorkflowListPath();
        Workflow[] workflows = given()
                .when()
                .get(url)
                .then()
                .extract().body().as(Workflow[].class);
        for (Workflow workflow : workflows) {
            if(workflow.getName().equalsIgnoreCase(workflowName))
                return workflow;
        }
        return null;
    }
}

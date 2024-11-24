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
public class FunctionValueProvider {

    @Value("${lenneflow.root.url}")
    private String rootUrl;

    @Value("${lenneflow.function.root.url}")
    private String functionRootUrl;

    @Value("${resource.function.json-schema.create}")
    private String createJsonSchemaPath;

    @Value("${resource.function.json-schema.find}")
    private String findJsonSchemaPath;

    @Value("${resource.function.create}")
    private String createFunctionPath;

    @Value("${resource.function.find}")
    private String findFunctionPath;

    @Value("${resource.function.delete}")
    private String deleteFunctionPath;

    @Value("${resource.function.find-all}")
    private String findAllFunctionsList;

    @Value("${resource.function.json-schema.delete}")
    private String deleteJsonSchemaPath;

    @Value("${resource.function.json-schema.find-all}")
    private String findJsonSchemaListPath;

    @Value("${resource.function.deploy}")
    private String deployFunctionPath;

    @Value("${resource.function.undeploy}")
    private String unDeployFunctionPath;


}

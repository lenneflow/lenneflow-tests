package de.lenneflow.lenneflowtests.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesReader {

    Properties prop = new Properties();

    public PropertiesReader(){
        String userDir = System.getProperty("user.home");
        try {
            prop.load(new InputStreamReader(new FileInputStream(userDir+"/lenneflow-test-data/test.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String key){
        return prop.getProperty(key);
    }

}

package com.bn.appium.tests.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesManager {
    private Properties properties;

    public PropertiesManager() {
        init();
    }

    public void init() {
        try {
            loadProperties();
        } catch (IOException e) {
            closeApp("Unable to load configuration file: " + MainConstants.CONFIGURATION_FILE_PATH);
        }
    }

    public String getProperty(String propertyKey) {
        Enumeration enuKeys = properties.keys();
        while (enuKeys.hasMoreElements()) {
            String key = (String) enuKeys.nextElement();
            if(!key.equalsIgnoreCase(propertyKey))
                continue;
            String value = properties.getProperty(key);
            return value;
        }
        return null;
    }

    private void loadProperties() throws IOException {
        File configProperties = new File(MainConstants.CONFIGURATION_FILE_PATH);
        FileInputStream fileInput = new FileInputStream(configProperties);
        Properties properties = new Properties();
        properties.load(fileInput);
        fileInput.close();
        setProperties(properties);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void closeApp(String s) {
        System.out.println(s);
        System.exit(0);
    }

}

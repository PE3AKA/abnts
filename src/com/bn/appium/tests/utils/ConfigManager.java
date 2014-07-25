package com.bn.appium.tests.utils;

/**
 * Created by nikolai on 23.06.2014.
 */
public class ConfigManager {
    private PropertiesManager propertiesManager;

    public ConfigManager() {
        propertiesManager = new PropertiesManager();
    }

    public PropertiesManager getPropertiesManager() {
        return propertiesManager;
    }

    public String getProperty(String configName){
        String property =  propertiesManager.getProperty(configName);
        System.out.println("Property:" + property);
        return property;
    }

}

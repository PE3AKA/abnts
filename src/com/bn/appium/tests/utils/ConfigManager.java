package com.bn.appium.tests.utils;

import static com.bn.appium.tests.utils.LoggerUtils.e;

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

    public int getTimeout(){
        String line = getProperty(ConfigurationParametersEnum.TIMEOUT.name());
        int timeout;
        try{
            timeout = Integer.parseInt(line);
        }catch (Throwable ex){
            e("Used default timeout = " + MainConstants.DEFAULT_TIMEOUT + ex.getMessage());
            timeout = MainConstants.DEFAULT_TIMEOUT;
        }
        return timeout;
    }

}

package com.bn.appium.tests.utils;

import net.bugs.testhelper.TestHelper;
import net.bugs.testhelper.helpers.PropertiesManager;

import static com.bn.appium.tests.utils.LoggerUtils.e;

/**
 * Created by nikolai on 12.02.14.
 */
public class Device {
    public String deviceId;
    public String build;
    public String network;
    public String os_system;
    public String hwDevice;
    public String osDevice;
    public int timeout;
    private TestHelper testHelper;
    private PropertiesManager propertiesManager;
    private String mBuildID = null;

    public Device(TestHelper testHelper, String buildID) {
        this.testHelper = testHelper;
        this.mBuildID = buildID;
        init();
    }

    public void init(){
        propertiesManager = testHelper.propertiesManager;
        if(mBuildID == null)
            build = propertiesManager.getProperty(ConfigurationParametersEnum.BUILD.name());
        else
            build = mBuildID;
        network = propertiesManager.getProperty(ConfigurationParametersEnum.NET.name());
        os_system = propertiesManager.getSystemOs();
        deviceId = testHelper.getDeviceID();
        hwDevice = testHelper.getHwDevice();
        osDevice = testHelper.getOsDevice();
        parseTimeout(propertiesManager.getProperty(ConfigurationParametersEnum.TIMEOUT.name()));
    }

    private void parseTimeout(String line){
        try{
            timeout = Integer.parseInt(line);
        }catch (Throwable ex){
            e("Used default timeout = " + MainConstants.DEFAULT_TIMEOUT + ex.getMessage());
            timeout = MainConstants.DEFAULT_TIMEOUT;
        }
    }

    public void setTimeout(String timeout) {
        if(timeout != null)
            parseTimeout(timeout);
    }

    public void setHwDevice(String hwDevice) {
        if(hwDevice != null)
            this.hwDevice = hwDevice;
    }
}

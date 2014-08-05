package com.bn.appium.tests;

import com.bn.appium.tests.android.AndroidKpiTests;
import com.bn.appium.tests.ios.IOsKpiTests;
import com.bn.appium.tests.manager.TestManager;
import com.bn.appium.tests.utils.ConfigManager;

/**
 * Created by nikolai on 25.07.2014.
 */
public class Main {

    public static void main(String[] args){
        ConfigManager configManager = new ConfigManager();
        switch (TestManager.getCurrentPlatform(configManager)){
            case Android:
                AndroidKpiTests androidKpiTests = new AndroidKpiTests();
                try {
                    androidKpiTests.setUp();
                    androidKpiTests.testOobe();
                    androidKpiTests.tearDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case iOs:
                IOsKpiTests iOsKpiTests = new IOsKpiTests();
                try {
                    iOsKpiTests.setUp();
                    iOsKpiTests.login();
                    iOsKpiTests.logOut();
                    iOsKpiTests.tearDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

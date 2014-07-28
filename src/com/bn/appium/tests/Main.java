package com.bn.appium.tests;

import com.bn.appium.tests.ios.IOsKpiTests;

/**
 * Created by nikolai on 25.07.2014.
 */
public class Main {

    public static void main(String[] args){
//        AndroidKpiTests androidKpiTests = new AndroidKpiTests();
//        try {
//            androidKpiTests.setUp();
//            androidKpiTests.testOobe();
//            androidKpiTests.tearDown();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        IOsKpiTests iOsKpiTests = new IOsKpiTests();
        try {
            iOsKpiTests.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            iOsKpiTests.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
        iOsKpiTests.logOut();
        try {
            iOsKpiTests.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

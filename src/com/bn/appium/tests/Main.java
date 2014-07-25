package com.bn.appium.tests;

import com.bn.appium.tests.android.AndroidKpiTests;

/**
 * Created by nikolai on 25.07.2014.
 */
public class Main {

    public static void main(String[] args){
        AndroidKpiTests androidKpiTests = new AndroidKpiTests();
        try {
            androidKpiTests.setUp();
            androidKpiTests.testOobe();
            androidKpiTests.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

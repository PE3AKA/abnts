package com.bn.appium.tests.utils;

/**
 * Created by nikolai on 10.01.14.
 */
public class LoggerUtils {
    public static final String SPLIT = "::";
    public static final String INFO = "INFO:";
    public static final String ERROR = "ERROR:";

    public static void i(String tag, String msg){
        System.out.println(INFO + tag + SPLIT + msg);
    }

    public static void i(String msg){
        System.out.println(INFO + SPLIT + msg);
    }

    public static void e(String tag, String msg){
        System.out.println(ERROR + tag + SPLIT + msg);
    }

    public static void e(String msg){
        System.out.println(ERROR + SPLIT + msg);
    }
}

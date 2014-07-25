package com.bn.appium.tests.manager;

import com.bn.appium.tests.utils.*;
import io.appium.java_client.AppiumDriver;
import net.bugs.testhelper.TestHelper;
import net.bugs.testhelper.view.View;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by nikolai on 10.01.14.
 */
public class TestManager {
    private static volatile TestManager instance;
    private static TestHelper testHelper;
    private static FileWorker fileWorker;
    private static FileWorker fileLogWorker;
    private static String mDeviceId = null;
    private static String mBuildId = null;
    private static String mLogin = null;
    private static String mPassword = null;
    private static String mHwDevice = null;
    private static String mArgTimeout = null;
    private static long mLastDumpTime = 0;
    private static boolean isAccuracy = false;
    public static int mTimeout;
    private boolean isStopErrorHandler = true;
    public static ArrayList<String> itemsName = new ArrayList<String>();
    public static Device mDevice;
    private static long mStartTime = 0;
    private static long mEndTime = 0;
    private static boolean mTestResult = false;
    public static ConfigManager configManager = null;
    private static AppiumDriver driver;

    private TestManager(String buildID, String deviceID, AppiumDriver appiumDriver) {
        driver = appiumDriver;
        configManager = new ConfigManager();
        testHelper = new TestHelper(deviceID);
        mDevice = new Device(testHelper, buildID);
        mDevice.setHwDevice(mHwDevice);
        mDevice.setTimeout(mArgTimeout);
        mTimeout = mDevice.timeout;
        fileWorker = new FileWorker(testHelper);
        fileLogWorker = new FileWorker(MainConstants.FILE_NAME_LOG_TESTS, testHelper);
    }

    public static TestManager getInstance(final String buildId, final String login, final String password,
                                          final String deviceId, final String hwDevice, final String timeout, AppiumDriver driver){
        mArgTimeout = timeout;
        mDeviceId = deviceId;
        mHwDevice = hwDevice;
        mBuildId = buildId;
        mLogin = login;
        mPassword = password;
        if(instance == null)
            synchronized (TestManager.class){
                if(instance == null)
                    instance = new TestManager(mBuildId, mDeviceId, driver);
            }
        return instance;
    }

    public static TestManager getInstance(AppiumDriver driver){
        return getInstance(mBuildId, mLogin, mPassword, mDeviceId, mHwDevice, mArgTimeout, driver);
    }

    public static void stopApplication(String $package){
        testHelper.executeShellCommand(" am force-stop " + $package, testHelper.defaultCallBack);
    }

    public static void runAppIntent(String intent){
        testHelper.executeShellCommand(" am start -n " + intent, testHelper.defaultCallBack);
    }


    public static ItemLog addLogParams(Date date, String testAction, String testData, boolean testResult){
        ItemLog itemLog = new ItemLog(testHelper);
        itemLog.setBuild(mDevice.build);
        itemLog.setDeviceId(mDevice.deviceId);
        itemLog.setNet(mDevice.network);
        itemLog.setHw(mDevice.hwDevice);
        itemLog.setOs(mDevice.osDevice);
        itemLog.setSlaveId(mDevice.os_system);
        itemLog.setDate(date, "");
        itemLog.setTime(date, "");
        itemLog.setStartTime(mStartTime);
        itemLog.setEndTime(mEndTime, mLastDumpTime);
        itemLog.setTestId("AlchemyKpi");
        itemLog.setTestAction(testAction);
        itemLog.setTestData(testData);
        itemLog.setTestResult(testResult);
        return itemLog;
    }

    private void setupItemsName() {
        File file = new File(MainConstants.PATH_TO_ITEMS_NAME);
        if(!file.exists()) {
            testHelper.i("file doesn't exist");
            return;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                if(line.startsWith("#")) continue;
                itemsName.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fileReader != null)
                    fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int generateRandom(int n){
        if(n == 0)
            return 0;
        return Math.abs(new Random().nextInt()) % n;
    }

    public static void write(ItemLog itemLog){
        fileLogWorker.writeLog(itemLog);
    }

    public static Date write(String text){
        testHelper.i(">>>>>>>>>>>>>>> TEST STEP: " + text);
        return fileWorker.write(text);
    }

    public TestHelper getTestHelper() {
        return testHelper;
    }

    public void startErrorHandler(final boolean isStrictMode, final boolean isTakeScreenShot){
        if(!isStopErrorHandler) return;
        isStopErrorHandler = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStopErrorHandler){
                    ArrayList<View> views = testHelper.getAllViews(false);
                    testHelper.sleep(1, false);
                    if(views == null || views.size() == 0) continue;
                    View synchronizeLibraryError = testHelper.getViewByText(Errors.Strings.SYNCHRONIZE_LIBRARY_ERROR_TITLE, true, 0, false);
                    View unknownError = testHelper.getViewByText(Errors.Strings.UNKNOWN_ERROR_TITLE, true, 0, false);
                    String textError = null;
                    if((synchronizeLibraryError != null && synchronizeLibraryError.exists() && (textError = synchronizeLibraryError.getText()) != null) ||
                            (unknownError != null && unknownError.exists() && (textError = unknownError.getText()) != null)) {
                        if(isStrictMode) System.exit(0);
                        Date date = write("ERROR: " + textError);
                        if(isTakeScreenShot){
                            testHelper.takeScreenshot(testHelper.getStringFromDate(date, MainConstants.TIME_FORMAT) + testHelper.getFieldNameInClassByValue(Errors.Strings.class, textError), MainConstants.PATH_TO_SCREENSHOTS);
                        }
                        View ok = testHelper.getViewByText(Errors.Strings.ERROR_OK, true, 0, false);
                        if(ok != null && ok.exists()){
                            ok.click();
                            testHelper.sleep(5000);
                        }
                    }
                }
            }
        }).start();
    }

    public void stopErrorHandler() {
        isStopErrorHandler = true;
    }


    public static void captureScreenshot(String testName) {
        new File("target/surefire-reports/screenshot/").mkdirs();
        String filename = "target/surefire-reports/screenshot/" + testName + ".jpg";

        try {
            WebDriver augmentedDriver = new Augmenter().augment(driver);
            File scrFile = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(filename), true);
        } catch (Exception e) {
            System.out.println("Error capturing screen shot of " + testName + " test failure.");
        }
    }

    public static class Errors {
        public static class Strings {
            public static final String SYNCHRONIZE_LIBRARY_ERROR_TITLE = "Problem occurred while trying to synchronize library. Please try again later.";
            public static final String UNKNOWN_ERROR_TITLE = "Unknown error";
            public static final String ERROR_OK = "OK";
        }
    }

    public static void pressBack(){
        testHelper.pressBack();
    }

    private static void timer(boolean isStart){
        long time = System.currentTimeMillis();
        testHelper.i(String.format ((isStart ? "timer started at %s" : "timer stopped at %s, total time: %s"), testHelper.getStringFromDate(new Date(time), "HH:mm:ss.SSS") + "", (time - mStartTime) + ""));
        if(isStart)
            mStartTime = time;
        else{
            if(isAccuracy){
                mLastDumpTime = testHelper.getLastDumpTotalTime();
                testHelper.i("Time dump: " + mLastDumpTime);
            }
            mEndTime = time;
        }
    }

    public static void startTimer(){
        timer(true);
    }

    public static void stopTimer(boolean accuracy){
        isAccuracy = accuracy;
        mLastDumpTime = 0;
        timer(false);
    }

    @Deprecated
    public static void stopTimer(){
        timer(false);
    }

    public static void testResult(boolean testResult){
        mTestResult = testResult;
    }
}

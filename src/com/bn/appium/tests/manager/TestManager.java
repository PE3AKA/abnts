package com.bn.appium.tests.manager;

import com.bn.appium.tests.exception.AppiumPlatformException;
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

import static com.bn.appium.tests.utils.LoggerUtils.i;

/**
 * Created by nikolai on 10.01.14.
 */
public class TestManager {
    private static volatile TestManager instance;
    private enum Platform {Android, iOs, None};
    public static Platform currentPlatform = Platform.None;

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
    public static ConfigManager configManager = null;
    private static AppiumDriver driver;

    private TestManager(String buildID, String deviceID, AppiumDriver appiumDriver) {
        driver = appiumDriver;
        configManager = new ConfigManager();
        currentPlatform = getCurrentPlatform();
        switch (currentPlatform){
            case Android:
                i("Android platform is selected!");
                testHelper = new TestHelper(deviceID);
                mDevice = new Device(testHelper, buildID);
                mDevice.setHwDevice(mHwDevice);
                mDevice.setTimeout(mArgTimeout);
                break;
            case iOs:
                i("iOs platform is selected!");
                break;
            case None:
                try {
                    throw new AppiumPlatformException("Please, specify the value of the MOBILE_PLATFORM!");
                } catch (AppiumPlatformException e) {
                    e.printStackTrace();
                }
                break;
        }

        mTimeout = configManager.getTimeout();
        fileWorker = new FileWorker();
        fileLogWorker = new FileWorker(MainConstants.FILE_NAME_LOG_TESTS);
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

    public static Platform getCurrentPlatform(){
        String mobilePlatform = configManager.getProperty(ConfigurationParametersEnum.MOBILE_PLATFORM.name());
        if(mobilePlatform == null || mobilePlatform.isEmpty())
            return Platform.None;
        if(mobilePlatform.toLowerCase().equals("android"))
            return Platform.Android;
        if(mobilePlatform.toLowerCase().equals("ios"))
            return Platform.iOs;
        return Platform.None;
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
        itemLog.setBuild(mDeviceId != null ? mDevice.build : "");
        itemLog.setDeviceId(mDeviceId != null ? mDevice.deviceId : "");
        itemLog.setNet(mDeviceId != null ? mDevice.network : "");
        itemLog.setHw(mDeviceId != null ? mDevice.hwDevice : "");
        itemLog.setOs(mDeviceId != null ? mDevice.osDevice : "");
        itemLog.setSlaveId(mDeviceId != null ? mDevice.os_system : "");
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
            if(testHelper != null)
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
        if(testHelper != null)
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
        if(testHelper != null)
            testHelper.i(String.format ((isStart ? "timer started at %s" : "timer stopped at %s, total time: %s"), testHelper.getStringFromDate(new Date(time), "HH:mm:ss.SSS") + "", (time - mStartTime) + ""));
        if(isStart)
            mStartTime = time;
        else{
            if(isAccuracy){
                mLastDumpTime = testHelper.getLastDumpTotalTime();
                if(testHelper != null)
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

    public static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void stopTimer(){
        timer(false);
    }

}

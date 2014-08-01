package com.bn.appium.tests.android;

import com.bn.appium.tests.manager.TestManager;
import com.bn.appium.tests.utils.ConfigManager;
import com.bn.appium.tests.utils.ConfigurationParametersEnum;
import com.bn.appium.tests.utils.MainConstants;
import com.bn.appium.tests.utils.Timer;
import io.appium.java_client.AppiumDriver;
import net.bugs.testhelper.TestHelper;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.net.URL;
import java.util.Date;

public class AndroidKpiTests {
    private AppiumDriver driver;
    private ConfigManager configManager;
    private TestManager testManager;
    private TestHelper testHelper;

    public void setUp() throws Exception {
        System.out.println("setUp");
        configManager = new ConfigManager();
        File appDir = new File(configManager.getProperty(ConfigurationParametersEnum.ANDROID_APP_DIR.name()));
        File app = new File(appDir, configManager.getProperty(ConfigurationParametersEnum.ANDROID_APP.name()));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability("platformName", configManager.getProperty(ConfigurationParametersEnum.ANDROID_PLATFORM_NAME.name()));
        capabilities.setCapability("deviceName", configManager.getProperty(ConfigurationParametersEnum.ANDROID_DEVICE_ID.name()));
        capabilities.setCapability("platformVersion", configManager.getProperty(ConfigurationParametersEnum.ANDROID_PLATFORM_VERSION.name()));
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("appPackage", configManager.getProperty(ConfigurationParametersEnum.ANDROID_APP_PACKAGE.name()));
        capabilities.setCapability("appActivity", configManager.getProperty(ConfigurationParametersEnum.ANDROID_APP_ACTIVITY.name()));
        driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        testManager = TestManager.getInstance(driver);
        testHelper = testManager.getTestHelper();
    }

    public void tearDown() throws Exception {
        driver.quit();
    }

    private boolean waitElement(final By by, long ms){
        WebDriverWait waiter = new WebDriverWait(driver, ms/1000);
        WebElement webElement = null;
        try {
            webElement = waiter.until(ExpectedConditions.visibilityOfElementLocated(by));
            if (webElement.isDisplayed())
                return true;
        }catch (Exception ex){
            return false;
        }
        return false;
    }

    public void testOobe() {
        TestManager.startTimer();
        MainConstants.TIME_START_TEST = System.currentTimeMillis();

        System.out.println("wait LOG IN");
        if (!waitElement(By.name("United States"), Timer.getTimeout())) {
            TestManager.stopTimer(false);
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.SIGN_IN, Constant.Account.ACCOUNT, false));
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, false));
            return;
        }

        driver.findElement(By.name("LOG IN")).click();

        TestManager.startTimer();
        if (!waitElement(By.name("Email"), Timer.getTimeout())) {
            TestManager.stopTimer(false);
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.SIGN_IN, Constant.Account.ACCOUNT, false));
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, false));
            return;
        }

        WebElement email = driver.findElement(By.name("Email"));
        email.sendKeys(configManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        driver.sendKeyEvent(4);

        if (!waitElement(By.name("Next"), Timer.getTimeout())){
            TestManager.stopTimer(false);
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.SIGN_IN, Constant.Account.ACCOUNT, false));
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, false));
            return;
        }

        driver.findElement(By.name("Next")).click();

        sleep(2500);

        WebElement password = driver.findElementByClassName("android.widget.EditText");
        password.sendKeys(configManager.getProperty(ConfigurationParametersEnum.PASSWORD.name()));
        driver.sendKeyEvent(4);

        if(!waitElement(By.name("Sign Up"), Timer.getTimeout())) {
            TestManager.stopTimer(false);
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.SIGN_IN, Constant.Account.ACCOUNT, false));
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, false));
            return;
        }

        driver.findElement(By.name("Sign Up")).click();

        TestManager.startTimer();
        if(!waitElement(By.name("Allow"), Timer.getTimeout())) {
            TestManager.stopTimer(false);
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.SIGN_IN, Constant.Account.ACCOUNT, false));
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, false));
            return;
        }

        TestManager.stopTimer(false);
        TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.SIGN_IN, Constant.Account.ACCOUNT, true));

        driver.findElementByName("Allow").click();

        String logcat = TestManager.configManager.getProperty(ConfigurationParametersEnum.SYNC_COMPLETE.name());
        if (!testHelper.waitForLogcatLineExists(logcat, Timer.getTimeout())) {
            TestManager.stopTimer(false);
            TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, false));
            return;
        }
        TestManager.stopTimer(false);
        TestManager.write(TestManager.addLogParams(new Date(), MainConstants.Android.Kpi.TestAction.FULL_SYNC, Constant.Account.ACCOUNT, true));
    }

    private static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void captureScreenshot(String testName) {
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

    public static class Constant{
        public static class Account{
            public static String ACCOUNT = "";
            public static String PASSWORD = "";
        }
    }
}

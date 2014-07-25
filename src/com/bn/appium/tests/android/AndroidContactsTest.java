package com.bn.appium.tests.android;

import com.bn.appium.tests.utils.ConfigManager;
import com.bn.appium.tests.utils.ConfigurationParametersEnum;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.net.URL;

public class AndroidContactsTest {
    private AppiumDriver driver;
    private ConfigManager configManager;

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

    public void addContact(){
        System.out.println("wait LOG IN");
        if(!waitElement(By.name("United States"), 60000))
            return;
        driver.findElement(By.name("LOG IN")).click();

        if(!waitElement(By.name("Email"), 60000))
            return;
        WebElement email = driver.findElement(By.name("Email"));
        email.sendKeys(configManager.getProperty(ConfigurationParametersEnum.LOGIN.name()));
        driver.sendKeyEvent(4);

        if(!waitElement(By.name("Next"), 60000))
            return;
        driver.findElement(By.name("Next")).click();

//        if(!waitElement(By.name("Password"), 60000))
//            return;
        sleep(2500);

        WebElement password = driver.findElementByClassName("android.widget.EditText");
        password.sendKeys(configManager.getProperty(ConfigurationParametersEnum.PASSWORD.name()));
        driver.sendKeyEvent(4);

        if(!waitElement(By.name("Sign Up"), 60000))
            return;
        driver.findElement(By.name("Sign Up")).click();

        sleep(15000);
    }

    private static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

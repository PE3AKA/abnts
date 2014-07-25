package com.bn.appium.tests.ios;

import com.bn.appium.tests.utils.ConfigManager;
import com.bn.appium.tests.utils.ConfigurationParametersEnum;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;

public class IOsKpiTests {
    private AppiumDriver driver;
    private WebElement row;
    private ConfigManager configManager;

    public void setUp() throws Exception {
        configManager = new ConfigManager();
        File classpathRoot = new File(System.getProperty("user.dir"));
//        File appDir = new File(classpathRoot, "../../../apps/UICatalog/build/Release-iphonesimulator");
//        File appDir = new File(classpathRoot, "../../../apps/WebViewApp/build/Release-iphonesimulator");
//        File app = new File(appDir, "WebViewApp.app");
//        File app = new File(appDir, "UICatalog.app");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability("platformVersion", configManager.getProperty(ConfigurationParametersEnum.IOS_PLATFORM_VERSION.name()));
        capabilities.setCapability("platformName", configManager.getProperty(ConfigurationParametersEnum.IOS_PLATFORM_NAME.name()));
        capabilities.setCapability("deviceName", configManager.getProperty(ConfigurationParametersEnum.IOS_DEVICE_ID.name()));
        capabilities.setCapability("U", configManager.getProperty(ConfigurationParametersEnum.IOS_DEVICE_ID.name()));
        capabilities.setCapability("app", configManager.getProperty(ConfigurationParametersEnum.IOS_APP_PACKAGE.name()));
        driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    public void tearDown() throws Exception {
        driver.quit();
    }

    private void openMenuPosition(int index) {
        //populate text fields with two random number
        MobileElement table = new MobileElement((RemoteWebElement)driver.findElementByClassName("UIATableView"), driver);
        row = table.findElementsByClassName("UIATableCell").get(index);
        row.click();
    }

    private Point getCenter(WebElement element) {

      Point upperLeft = element.getLocation();
      Dimension dimensions = element.getSize();
      return new Point(upperLeft.getX() + dimensions.getWidth()/2, upperLeft.getY() + dimensions.getHeight()/2);
    }

    public void testFindElement() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
        System.out.println(simpleDateFormat.format(System.currentTimeMillis()) + " start test");
        MobileElement signInButton = new MobileElement((RemoteWebElement)driver.findElementByName("signIn"), driver);

        if(signInButton.isDisplayed()) {
            System.out.println(simpleDateFormat.format(System.currentTimeMillis()) + " Click");
            signInButton.click();
        }
        Thread.sleep(2000);

        WebElement editText = driver.findElementByClassName("UIATextField");
        editText.sendKeys("8494076_qa@books.com");

        WebElement secureEditText = driver.findElementByClassName("UIASecureTextField");
        secureEditText.sendKeys("access\n");

//        WebElement signIn = driver.findElementByName("Sign in");
//
//        if(signIn.isDisplayed()) {
//            System.out.println(simpleDateFormat.format(System.currentTimeMillis()) + " click sign in");
//            signIn.click();
//        }


        Thread.sleep(2000);

        WebElement webElement = null;
        try {
            webElement = driver.findElementByName("Network connection in progress");
        } catch (Exception ex) {
            webElement = null;
        }

        while (webElement != null) {
            System.out.println(simpleDateFormat.format(System.currentTimeMillis()) + " wait for signed in");
            try {
                webElement = driver.findElementByName("Network connection in progress");
            } catch (Exception ex) {
                webElement = null;
            }
        }
    }
}

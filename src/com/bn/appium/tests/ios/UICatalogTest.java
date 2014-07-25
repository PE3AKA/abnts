package com.bn.appium.tests.ios;

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


/**
 * <a href="https://github.com/appium/appium">Appium</a> test which runs against a local Appium instance deployed
  * with the 'UICatalog' iPhone project which is included in the Appium source distribution.
 *
 * @author Ross Rowe
 */
public class UICatalogTest {

    private static AppiumDriver driver;

    private WebElement row;

    public static void main(String[] args){
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            testFindElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUp() throws Exception {
        // set up appium
        File classpathRoot = new File(System.getProperty("user.dir"));
//        File appDir = new File(classpathRoot, "../../../apps/UICatalog/build/Release-iphonesimulator");
//        File appDir = new File(classpathRoot, "../../../apps/WebViewApp/build/Release-iphonesimulator");
//        File app = new File(appDir, "WebViewApp.app");
//        File app = new File(appDir, "UICatalog.app");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability("platformVersion", "7.1.2");
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("deviceName", "87fcb473c35ef455aefb93db35ec904c12086da9");
        capabilities.setCapability("U", "87fcb473c35ef455aefb93db35ec904c12086da9");
        capabilities.setCapability("app", "com.barnesandnoble.B-N-eReader");

        driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    public static void tearDown() throws Exception {
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

    public static void testFindElement() throws Exception {
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

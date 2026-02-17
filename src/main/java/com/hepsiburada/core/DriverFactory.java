package com.hepsiburada.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {
    private static final String CHROME = "chrome";
    private static final String FIREFOX = "firefox";
    private static final String EDGE = "edge";

    public static WebDriver createDriver() {
        String browser = System.getProperty("browser", CHROME).toLowerCase();
        WebDriver driver;

        switch (browser) {
            case FIREFOX:
                driver = new FirefoxDriver(new FirefoxOptions());
                break;
            case EDGE:
                driver = new EdgeDriver(new EdgeOptions());
                break;
            case CHROME:
            default:
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--disable-notifications");
                options.addArguments("--start-maximized");
                driver = new ChromeDriver(options);
                break;
        }

        driver.manage().window().maximize();
        return driver;
    }
}

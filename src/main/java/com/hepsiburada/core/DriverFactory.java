package com.hepsiburada.core;

import com.hepsiburada.utils.Config;
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

    private static final String[] CHROME_ARGS = {
            "--disable-popup-blocking",
            "--disable-notifications",
            "--start-maximized",
            "--disable-blink-features=AutomationControlled"
    };
    private static final String[] EDGE_ARGS = {
            "--disable-popup-blocking",
            "--disable-notifications"
    };
    private static final String[] FIREFOX_ARGS = {"--disable-notifications"};

    public static WebDriver createDriver() {
        String browser = Config.getBrowser().toLowerCase();
        WebDriver driver;

        switch (browser) {
            case FIREFOX:
                FirefoxOptions fo = new FirefoxOptions();
                for (String arg : FIREFOX_ARGS) fo.addArguments(arg);
                driver = new FirefoxDriver(fo);
                break;
            case EDGE:
                EdgeOptions eo = new EdgeOptions();
                for (String arg : EDGE_ARGS) eo.addArguments(arg);
                driver = new EdgeDriver(eo);
                break;
            case CHROME:
            default:
                ChromeOptions co = new ChromeOptions();
                for (String arg : CHROME_ARGS) co.addArguments(arg);
                driver = new ChromeDriver(co);
                break;
        }

        driver.manage().window().maximize();
        return driver;
    }
}

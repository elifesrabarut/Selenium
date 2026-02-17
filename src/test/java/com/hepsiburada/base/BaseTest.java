package com.hepsiburada.base;

import com.hepsiburada.core.DriverFactory;
import com.hepsiburada.core.DriverManager;
import com.hepsiburada.utils.Config;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}

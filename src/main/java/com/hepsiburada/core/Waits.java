package com.hepsiburada.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class Waits {
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    public static WebDriverWait createWait(WebDriver driver, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        return waitForVisibility(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public static WebElement waitForVisibility(WebDriver driver, By locator, int timeoutSeconds) {
        return createWait(driver, timeoutSeconds)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return waitForClickable(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public static WebElement waitForClickable(WebDriver driver, By locator, int timeoutSeconds) {
        return createWait(driver, timeoutSeconds)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return waitForPresence(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public static WebElement waitForPresence(WebDriver driver, By locator, int timeoutSeconds) {
        return createWait(driver, timeoutSeconds)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForUrlContains(WebDriver driver, String urlPart) {
        return waitForUrlContains(driver, urlPart, DEFAULT_TIMEOUT_SECONDS);
    }

    public static boolean waitForUrlContains(WebDriver driver, String urlPart, int timeoutSeconds) {
        return createWait(driver, timeoutSeconds)
                .until(ExpectedConditions.urlContains(urlPart));
    }

    public static boolean waitForInvisibility(WebDriver driver, By locator) {
        return waitForInvisibility(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public static boolean waitForInvisibility(WebDriver driver, By locator, int timeoutSeconds) {
        try {
            return createWait(driver, timeoutSeconds)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static <T> T waitUntil(WebDriver driver, ExpectedCondition<T> condition) {
        return waitUntil(driver, condition, DEFAULT_TIMEOUT_SECONDS);
    }

    public static <T> T waitUntil(WebDriver driver, ExpectedCondition<T> condition, int timeoutSeconds) {
        return createWait(driver, timeoutSeconds).until(condition);
    }

    public static Boolean waitUntil(WebDriver driver, Function<WebDriver, Boolean> condition) {
        return waitUntil(driver, condition, DEFAULT_TIMEOUT_SECONDS);
    }

    public static Boolean waitUntil(WebDriver driver, Function<WebDriver, Boolean> condition, int timeoutSeconds) {
        return createWait(driver, timeoutSeconds).until(condition);
    }
}

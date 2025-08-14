package com.example.service.aggregator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SeleniumManager {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Duration duration;

    public SeleniumManager() {
        driver = new DriverChrome().getDriverChrome();
        duration = Duration.ofSeconds(10);
        wait = new WebDriverWait(driver, duration);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public Duration getDuration() {
        return duration;
    }
}

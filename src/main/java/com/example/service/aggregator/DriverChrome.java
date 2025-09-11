package com.example.service.aggregator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverChrome {

    public WebDriver getDriverChrome() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // без окна

        WebDriver chromeDriver = new ChromeDriver(options);

        /*
         https://www.selenium.dev/documentation/webdriver/waits/
         */
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        return chromeDriver;
    }

}

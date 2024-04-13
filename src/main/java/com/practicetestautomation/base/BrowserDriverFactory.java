package com.practicetestautomation.base;

import java.util.logging.Level;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class BrowserDriverFactory {

	private ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private String browser;
	private Logger log;

	public BrowserDriverFactory(String browser, Logger log) {
		this.browser = browser.toLowerCase();
		this.log = log;
	}


	public WebDriver createDriver() {
		log.info("Create local driver: " + browser);

		switch (browser) {
		case "chrome":
			// Make sure to upgrade chromedriver to work with your browser version: https://chromedriver.chromium.org/downloads
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
			System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
			driver.set(new ChromeDriver());
			break;

		case "firefox":
			// Make sure to upgrade geckodriver to work with your browser version: https://github.com/mozilla/geckodriver/releases
			System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
//			System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
			//С версии Selenium 3.x и выше использование geckodriver стало обязательным для Firefox, и свойство
			// DRIVER_USE_MARIONETTE больше не нужно, так как Marionette (протокол взаимодействия geckodriver и Firefox)
			// используется по умолчанию. Т.е. строка излишня
//			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
			//Возможно, стоит заменить на:
			// FirefoxOptions options = new FirefoxOptions();
			//options.setLogLevel(FirefoxDriverLogLevel.ERROR); // Установите уровень логирования по вашему усмотрению
			//WebDriver driver = new FirefoxDriver(options);
			driver.set(new FirefoxDriver());
			break;
		case "safari":
			// Для Safari дополнительные настройки не требуются
			driver.set(new SafariDriver());
			break;
		default:
			log.debug("Do not know how to start: " + browser + ", starting chrome.");
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
			System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
			driver.set(new ChromeDriver());
			break;
		}
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
		return driver.get();
	}
}

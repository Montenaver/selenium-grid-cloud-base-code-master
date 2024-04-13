package com.practicetestautomation.base;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SauceLabsFactory implements SauceOnDemandAuthenticationProvider{

	private ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private String browser;
	private String platform;
	private Logger log;
	private String name;
	private ThreadLocal<String> sessionId = new ThreadLocal<>();

	private static final SauceOnDemandAuthentication AUTHENTICATION = new SauceOnDemandAuthentication(
			System.getProperty("sauce.username"),
			System.getProperty("sauce.accesskey"));

	public SauceLabsFactory(String browser, String platform, Logger log, String name) {
		this.browser = browser.toLowerCase();
		this.platform = platform;
		this.log = log;
		this.name = name;
	}


	public WebDriver createDriver() {
		log.info("Create SauceLabs instance for: " + browser + " on " + platform);
//		DesiredCapabilities capabilities = new DesiredCapabilities();//deprecated solution
//		capabilities.setCapability("browserName", browser);
//		capabilities.setCapability("browserVersion", "latest");
//		capabilities.setCapability("platformName", platform);

//		MutableCapabilities sauceOptions = new MutableCapabilities();
//
//		if (platform.contains("Windows")){
//			sauceOptions.setCapability("screenResolution", "1920x1080");
//		}else{
//			sauceOptions.setCapability("screenResolution", "1920x1440");
//		}
//
//		capabilities.setCapability("screenResolution", sauceOptions);


		MutableCapabilities browserOptions;

		switch (browser) {
			case "chrome":
				browserOptions = new ChromeOptions();
				break;

			case "firefox":
				browserOptions = new FirefoxOptions();
				break;

			case "safari":
				browserOptions = new SafariOptions();
				break;

			default:
				browserOptions = new ChromeOptions();
				break;
		}

		browserOptions.setCapability("platformName", platform);
		browserOptions.setCapability("browserVersion", "latest");
		Map<String, Object> sauceOptions = new HashMap<>();
		sauceOptions.put("username", AUTHENTICATION.getUsername());
		sauceOptions.put("accessKey", AUTHENTICATION.getAccessKey());
		sauceOptions.put("name", name);
		browserOptions.setCapability("sauce:options", sauceOptions);


		URL url = null;
		try {
			url = new URL("https://" + AUTHENTICATION.getUsername() + ":" +
					AUTHENTICATION.getAccessKey() + "@ondemand.eu-central-1.saucelabs.com:443/wd/hub");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		driver.set(new RemoteWebDriver(url, browserOptions));
		sessionId.set(((RemoteWebDriver) driver.get()).getSessionId().toString());

		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
		return driver.get();
	}

	@Override
	public SauceOnDemandAuthentication getAuthentication() {
		return AUTHENTICATION;
	}

	public String getSessionId() {
		return sessionId.get();
	}
}

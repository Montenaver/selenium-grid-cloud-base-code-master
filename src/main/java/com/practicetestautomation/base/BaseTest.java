package com.practicetestautomation.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

@Listeners({SauceLabsTestListener.class})
public class BaseTest {

	protected static WebDriver driver;
	protected Logger log;
	protected boolean sauce;

	@Parameters({ "browser", "environment", "platform" })
	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method, @Optional("chrome") String browser, @Optional("local") String environment, @Optional("mac") String platform, ITestContext ctx) {
		log = LogManager.getLogger(ctx.getCurrentXmlTest().getSuite().getName());
		setProperties();

		ctx.setAttribute("sauce", false);
		switch (environment){
			case "local":
				driver = new BrowserDriverFactory(browser, log).createDriver();
				break;
			case "grid":
				driver = new GridFactory(browser, platform, log).createDriver();
				break;
			case "sauce":
				ctx.setAttribute("sauce", true);
				String sauceTestName = ctx.getName() + " | " + method.getName() + " | " + browser + " | " + platform;
				SauceLabsFactory factory = new SauceLabsFactory(browser, platform, log, sauceTestName);
				driver = factory.createDriver();
				ctx.setAttribute("sessionId", factory.getSessionId());
				break;
			default:
				driver = new BrowserDriverFactory(browser, log).createDriver();
				break;
		}

		driver.manage().window().maximize();
	}

	private void setProperties() {
		Properties props = System.getProperties();
		try {
			props.load(new FileInputStream(new File("src/main/resources/test.properties")));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@AfterMethod
	public void tearDown() {
		log.info("Close driver");
		driver.quit();
	}
}

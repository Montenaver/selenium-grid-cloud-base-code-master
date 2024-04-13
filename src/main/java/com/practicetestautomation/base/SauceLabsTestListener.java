package com.practicetestautomation.base;

import com.saucelabs.saucerest.SauceREST;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterMethod;

import static com.practicetestautomation.base.BaseTest.driver;

public class SauceLabsTestListener extends TestListenerAdapter {
    private boolean sauce;
    private String sessionId;
    private SauceREST sauceREST;
    @Override
    //Проверяем запускаем ли мы тест в SauceLabs, и если да, то получаем  sessionId и создаем SauceOnDemandJavaRestAPI client
    public void onTestStart(ITestResult result){
        super.onTestStart(result);
        sauce = (boolean)result.getTestContext().getAttribute("sauce");
        if (sauce){
            this.sessionId = (String) result.getTestContext().getAttribute("sessionId");
            this.sauceREST = new SauceREST(System.getProperty("sauce.username"),
                    System.getProperty("sauce.accesskey"));
        }
    }

    @Override
    public void onTestSuccess(ITestResult result){
        super.onTestSuccess(result);//вначале выполняем то, что изначально было заложеено в этом методе, а потом добавляем свою собственную функциональность
        if (sauce) {
//            sauceREST.jobPassed(sessionId);
            ((JavascriptExecutor) driver).executeScript("sauce:job-result=passed");
        }
    }
    @Override
    public void onTestFailure(ITestResult result) {
        super.onTestFailure(result);
        if (sauce) {
//            sauceREST.jobFailed(sessionId);
            ((JavascriptExecutor) driver).executeScript("sauce:job-result=failed");
            Throwable throwable = result.getThrowable();
            String message = throwable.getMessage();
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            String link = "SL job link: https://app.eu-central-1.saucelabs.com/tests/" + sessionId;
            String sauceTestName = result.getTestContext().getName() + " | " + result.getName();

            String newMessage = sauceTestName + "\n" + link + "\n" + message;
            Throwable newThrowable = new Throwable(newMessage, throwable);
            newThrowable.setStackTrace(stackTrace);
            result.setThrowable(newThrowable);
        }
    }
}

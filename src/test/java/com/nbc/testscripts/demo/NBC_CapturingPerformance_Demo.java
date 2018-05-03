package com.nbc.testscripts.demo;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.nbc.pages.HomePage;
import com.nbc.support.*;

import net.lightbody.bmp.core.har.Har;

@Listeners(EmailReport.class)
public class NBC_CapturingPerformance_Demo extends BaseTest {

	String notes = null;
	String testLinkResult = null;
	String doHar = "true";
	protected String status = null;
	EnvironmentPropertiesReader environmentPropertiesReader;

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc002_Performance_Capture(String browser, String webSiteWithStakeHolder) throws Exception {

		// Get the web driver instance
		final WebDriver driver = WebDriverFactory.get(browser, true);
		
		String site = webSiteWithStakeHolder.split("_")[0];
		String stakeHolderName = webSiteWithStakeHolder.split("_")[1];
		Log.testCaseInfo("Performance Capture - HAR", Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase(), "PoC_Demo",
				"Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");
			Log.assertThatExtentReport(homePage.validateLogo(), "Validation 1: Branding Logo available as expected!",
					"Branding Logo not available on the page", driver);

			homePage.navigateToNewsPage();
			Log.message("Step 2. Navigated to 'News' Page!");
			Log.assertThatExtentReport(homePage.validateLogo(), "Validation 2: Branding Logo available as expected!",
					"Branding Logo not available on the page", driver);

			homePage.clickOnLogo();
			Log.message("Step 3. Clicked on Brand Logo!");
			Log.assertThatExtentReport(homePage.validateHomePage(), "Validation 3: Navigated to Home Page as expected!",
					"Failed to navigate to Home Page", driver);
			Log.assertThatExtentReport(homePage.validateLogo(), "Validation 4: Branding Logo available as expected!",
					"Branding Logo not available on the page", driver);
			Log.message(WebDriverFactory.sFileName);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc002

	@AfterMethod(alwaysRun = true)
	public final void tearDown(ITestResult result) throws IOException {
		status = "PASS";
		environmentPropertiesReader = new EnvironmentPropertiesReader();
		if ((System.getProperty("har").equalsIgnoreCase("true")) || (doHar == "true")) {
			// get the HAR data
			Har har = WebDriverFactory.proxy.getHar();

			// Write HAR Data in a File
			File harFile = new File(WebDriverFactory.sFileName);
			try {
				har.writeTo(harFile);
			} catch (IOException ex) {
			}

			WebDriverFactory.proxy.stop();
		}
	}// tearDown

}// NBC_Demo
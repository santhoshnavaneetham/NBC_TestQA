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
public class NBC_SpellChecker_BrokenLine_Demo extends BaseTest {

	String notes = null;
	String testLinkResult = null;
	protected String status = null;
	private static String xltestDataWorkBook;
	private static String xltestDataWorkSheet;
	EnvironmentPropertiesReader environmentPropertiesReader;

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc001(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_SpellChecker_BrokenLine_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_SpellChecker_BrokenLine_Demo.xltestDataWorkSheet = "Demo";

		// Get the web driver instance
		final WebDriver driver = WebDriverFactory.get(browser, true);
		final TestDataExtractor testData = new TestDataExtractor();

		// Loading the test data from excel using the test case id
		testData.setWorkBookName(xltestDataWorkBook);
		testData.setWorkSheet(xltestDataWorkSheet);
		testData.setFilePathMapping(true);
		testData.setTestCaseId(Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
		testData.readData();

		String site = webSiteWithStakeHolder.split("_")[0];
		String stakeHolderName = webSiteWithStakeHolder.split("_")[1];
		Log.testCaseInfo("Spelling and Broken Link Check", "TC001", "PoC_Demo",
				"Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			homePage.checkPageSpelling();
			Log.message("Validation 1. Done Spell Check on the Home Page!");

			Log.message("Validation 2: Broken Links on Page --> " + homePage.checkBrokenLinks());

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc001

	@AfterMethod(alwaysRun = true)
	public final void tearDown(ITestResult result) throws IOException {
		status = "PASS";
		environmentPropertiesReader = new EnvironmentPropertiesReader();

		if (System.getProperty("har") == "true") {
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
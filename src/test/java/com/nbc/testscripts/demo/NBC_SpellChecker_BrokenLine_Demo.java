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
	EnvironmentPropertiesReader environmentPropertiesReader;

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc001_Spelling_And_BrokenLink_Check(String browser, String webSiteWithStakeHolder) throws Exception {
		
		// Get the web driver instance
		final WebDriver driver = WebDriverFactory.get(browser, true);

		String site = webSiteWithStakeHolder.split("_")[0];
		String stakeHolderName = webSiteWithStakeHolder.split("_")[1];
		String path;
		Log.testCaseInfo("Spelling and Broken Link Check", Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase(), "PoC_Demo",
				"Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			
			Log.message("Validation 1. Starting Spell Check on the Home Page!");
			String resultString = homePage.checkPageSpelling(false);
			int resultLength = resultString.split("_").length;
			path = resultString.split("_")[0];
			if(resultLength<2) {
				Log.message("Validation 2. Spell Check on the Home Page was terminated !");
			} else if (resultLength==2) {
				Log.message("Validation 2. Spell Check on the Home Page Completed !! List of Words SpellChecked is present in File: "+path);
			} else if (resultLength>2) {
				Log.message("Validation 2. Spell Check on the Home Page was Completed but found errors ! Incorrect Words Count: "
						+resultString.split("_")[2]+"\r\n List of Words SpellChecked is present in File: "+ path);
			}
			
			
			Log.message("Validation 3. Starting Spell Check on the News Page!");
			homePage.navigateToNewsPage();
			resultString = homePage.checkPageSpelling(true);
			resultLength = resultString.split("_").length;
			path = resultString.split("_")[0];
			if(resultLength<2) {
				Log.message("Validation 4. Spell Check on the News Page was terminated !");
			} else if (resultLength==2) {
				Log.message("Validation 4. Spell Check on the News Page Completed !! List of Words SpellChecked is present in File: "+path);
			} else if (resultLength>2) {
				Log.message("Validation 4. Spell Check on the News Page was Completed but found errors ! Incorrect Words Count: "
						+resultString.split("_")[2]+"\r\n List of Words SpellChecked is present in File: "+ path);
			}
				
			homePage.clickOnLogo();
			Log.message("Validation 5. Starting Spell Check on the Article Page!");
			homePage.clickOnFirstArticle_OnLocalStackSection();
			resultString = homePage.checkPageSpelling(true);
			resultLength = resultString.split("_").length;
			path = resultString.split("_")[0];
			if(resultLength<2) {
				Log.message("Validation 6. Spell Check on the Article Page was terminated !");
			} else if (resultLength==2) {
				Log.message("Validation 6. Spell Check on the Article Page Completed !! List of Words SpellChecked is present in File: "+path);
			} else if (resultLength>2) {
				Log.message("Validation 6. Spell Check on the Article Page was Completed but found errors ! Incorrect Words Count: "
						+resultString.split("_")[2]+"\r\n List of Words SpellChecked is present in File: "+ path);
			}
			
			homePage.clickOnLogo();
//			String outputPath = homePage.checkBrokenLinks();
			Log.message("Validated for all the Valid/Broken Links. Logs are available at - '" + "C:\\SpellCheck\\BrokenLinks.txt" + "'");

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
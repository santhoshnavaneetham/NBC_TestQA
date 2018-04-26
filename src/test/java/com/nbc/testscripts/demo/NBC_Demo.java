package com.nbc.testscripts.demo;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.nbc.pages.ArticlePage;
import com.nbc.pages.HomePage;
import com.nbc.support.*;

import net.lightbody.bmp.core.har.Har;

@Listeners(EmailReport.class)
public class NBC_Demo extends BaseTest {

	String notes = null;
	String testLinkResult = null;
	protected String status = null;
	private static String xltestDataWorkBook;
	private static String xltestDataWorkSheet;
	EnvironmentPropertiesReader environmentPropertiesReader;

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc001(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC001 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

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

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc002(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC002 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			ArticlePage articlePage = homePage.clickOnFirstArticleWithVideo_OnTopSection();
			Log.message("Step 2. Navigated to First Article with Video on Top Section!");

			/*Log.assertThatExtentReport(articlePage.validateArticleVideoPlayer(),
					"Validation 1: Validated Article Video Player!", "Article Video Player failed to load",
					driver);*/


			Log.assertThatExtentReport(articlePage.validatePrerollVideo(),
					"Validation 1: Validated Pre-roll Video !", "Pre-roll Video failed to load",
					driver);

			// ** Yet to pre-roll validation. I couldn't see any pre-roll when i am
			// developing. may be geographical condition.

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

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc003(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC003 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			Log.assertThatExtentReport(homePage.validateWeatherForDays(),
					"Validation 1: Validated weather details displayed for future three days!",
					"Weather details not displayed for future three days", driver);
			Log.assertThatExtentReport(homePage.validateWeatherModule(), "Validation 2: Validated Weather Module!",
					"Weather Module failed to load", driver);
			// Log.assertThatExtentReport(homePage.validateWeatherAlertModule(),
			// "Validation 3: Validated Weather Alert Module!", "Weather Alert Module failed
			// to load",
			// driver);
			Log.assertThatExtentReport(homePage.validateRadarOnWeatherModule(),
					"Validation 4: Validated Radar in Weather Module!", "Failed to validate Radar",
					driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc003

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc004(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC004 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			Log.assertThatExtentReport(homePage.validateSpredfastModule(),
					"Validation 1: Validated Spredfast Module. Module loaded on Right Rail as expected!",
					"Spredfast Module failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc004

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc005(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC005 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			Log.assertThatExtentReport(homePage.validateSpredfastFeed(),
					"Validation 1: Validated Feed in spredfast. All Feed loaded Seconds/Minutes/Hours Ago!",
					"Feed in spredfast failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc005

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc006(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC006 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			homePage.doMouseHoverOnWatchLiveTVLink();
			Log.message("Step 2. Mouse Hover on Connect Link!");

			Log.assertThatExtentReport(homePage.validateWatchLiveTVHoverPanel(),
					"Validation 1: Validated Watch Live TV Hover Panel - Live TV , On Now, On Demand and Full Schedule!",
					"Watch Live TV Hover Panel failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc006

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc007(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC007 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			homePage.doMouseHoverOnWatchLiveTVLink();
			Log.message("Step 2. Mouse Hover on Connect Link!");

			homePage.navigateToFullScheduleOnLiveTV();
			Log.message("Step 3. Navigated to Full Schedule On Live TV!");

			Log.assertThatExtentReport(homePage.validateTVListing(),
					"Validation 1: Validated TV Listing - TV Listing Page, TV Tabs, Date Picker and Now TV!",
					"TV Listing failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc007

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc008(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC008 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			homePage.doMouseHoverOnConnectLink();
			Log.message("Step 2. Mouse Hover on Connect Link!");

			Log.assertThatExtentReport(homePage.validateConnectHoverPanel(),
					"Validation 1: Validated Connect Hover Panel - Media , Responds and Footer!",
					"Connect Hover Panel failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc008

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc009(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

		// Get the web driver instance
		final WebDriver driver = WebDriverFactory.get(browser, true);
		final TestDataExtractor testData = new TestDataExtractor();
		int validationPoints = 1;

		// Loading the test data from excel using the test case id
		testData.setWorkBookName(xltestDataWorkBook);
		testData.setWorkSheet(xltestDataWorkSheet);
		testData.setFilePathMapping(true);
		testData.setTestCaseId(Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
		testData.readData();

		String site = webSiteWithStakeHolder.split("_")[0];
		String stakeHolderName = webSiteWithStakeHolder.split("_")[1];
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC009 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			homePage.doMouseHoverOnConnectLink();
			Log.message("Step 2. Mouse Hover on Connect Link!");

			Log.assertThatExtentReport(homePage.validateConnectTwitter(),
					"Validation "+validationPoints+": Validated Connect Twitter Link!", "Connect Twitter Link failed to load",
					driver);

			if(!"https://www.nbcwashington.com".equals(site)) {
				Log.assertThatExtentReport(homePage.validateConnectInstagram(),
						"Validation "+(++validationPoints)+": Validated Connect Instagram Link!", "Connect Instagram Link failed to load",
						driver);
			}
			Log.assertThatExtentReport(homePage.validateConnectFacebook(),
					"Validation "+(++validationPoints)+": Validated Connect Facebook Link!", "Connect Facebook Link failed to load",
					driver);
			Log.assertThatExtentReport(homePage.validateConnectApps(), "Validation "+(++validationPoints)+": Validated Connect Apps Link!",
					"Connect Apps Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateConnectEmailNewsLetters(),
					"Validation "+(++validationPoints)+": Validated Connect Emails/NewsLetter Link!",
					"Connect Emails/NewsLetter Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateConnectSendUsVideosandPictures(),
					"Validation "+(++validationPoints)+": Validated Connect Send Us Videos and Pictures Link!",
					"Connect Send Us Videos and Pictures Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateConnectSendTips(),
					"Validation "+(++validationPoints)+": Validated Connect SendTips Link!", "Connect SendTips Link failed to load",
					driver);
			Log.assertThatExtentReport(homePage.validateFooterConnectSendTips(),
					"Validation "+(++validationPoints)+": Validated Footer Connect SendTips Link!",
					"Footer Connect SendTips Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateFooterConnectSendFeedback(),
					"Validation "+(++validationPoints)+": Validated Footer Connect Send Feedback Link!",
					"Footer Connect Send Feedback Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateFooterConnectTermsofService(),
					"Validation "+(++validationPoints)+": Validated Footer Connect Terms of Service Link!",
					"Footer Connect Terms of Service Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateFooterConnectPrivacyPolicy(),
					"Validation "+(++validationPoints)+": Validated Footer Connect Privacy Policy Link!",
					"Footer Connect Privacy Policy Link failed to load", driver);
			Log.assertThatExtentReport(homePage.validateFooterConnectPartner(),
					"Validation "+(++validationPoints)+": Validated Footer Connect Partner Link!",
					"Footer Connect Partner Link failed to load", driver);

			Log.testCaseResultExtentReport(driver);


		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc009

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc010(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC010 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			ArticlePage articlePage = homePage.clickOnFirstArticle_OnLocalStackSection();
			Log.message("Step 2. Navigated to First Article on Local Stack Section!");

			Log.assertThatExtentReport(articlePage.validateFacebookIcon(), "Validation 1: Validated Facebook Icon!",
					"Facebook Icon failed to load", driver);
			Log.assertThatExtentReport(articlePage.validateTwitterIcon(), "Validation 2: Validated Twitter Icon!",
					"Twitter Icon failed to load", driver);
			Log.assertThatExtentReport(articlePage.validateCommentsIcon(), "Validation 3: Validated Comments Icon!",
					"Comments Icon failed to load", driver);
			Log.assertThatExtentReport(articlePage.validateEmailIcon(), "Validation 4: Validated Email Icon!",
					"Email Icon failed to load", driver);
			Log.assertThatExtentReport(articlePage.validatePrintIcon(), "Validation 5: Validated Print Icon!",
					"Print Icon failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc010

	@Test(dataProviderClass = DataProviderUtils.class, dataProvider = "multiBrowserWebsites")
	public void tc011(String browser, String webSiteWithStakeHolder) throws Exception {

		NBC_Demo.xltestDataWorkBook = "testdata\\data\\PoC.xls";
		NBC_Demo.xltestDataWorkSheet = "Demo";

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
		Log.testCaseInfo(
				testData.get("Description") + "[" + browser + " || " + stakeHolderName.toUpperCase() + " ]",
				"TC011 - " + stakeHolderName.toUpperCase(), "PoC_Demo", "Aspire Systems", driver);

		try {

			HomePage homePage = new HomePage(driver, site).get();
			Log.message("Step 1. Navigated to '" + stakeHolderName + "' Home Page!");

			ArticlePage articlePage = homePage.navigateToArticleWithAdvacneTagging();
			Log.message("Step 2. Navigated to Article with Advacne Tagging Page!");

			Log.assertThatExtentReport(articlePage.validateHTMLModule(), "Validation 1: Validated HTML Module!",
					"HTML Module failed to load", driver);
			Log.assertThatExtentReport(articlePage.validateHTMLModule_Toolbar(),
					"Validation 2: Validated HTML Module Tool bar - Back Home, Zoom-In and Zoom-Out!",
					"HTML Module Tool bar failed to load", driver);
			Log.assertThatExtentReport(articlePage.validateHTMLModule_TimeNavigation(),
					"Validation 3: Validated HTML Module Time Nav - Time Nav Indicator and Time Nav Interval!",
					"HTML Module Time Nav failed to load", driver);
			Log.assertThatExtentReport(articlePage.validateHTMLModuleSlideContainer(),
					"Validation 4: Validated HTML Module Slide Container!",
					"HTML Module Slide Container failed to load", driver);

			Log.testCaseResultExtentReport(driver);

		} // try
		catch (Exception e) {
			Log.exception(e, driver);
		} // catch
		finally {
			Log.endTestCase();
			driver.quit();
		} // finally

	}// tc011

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
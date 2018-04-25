package com.nbc.support;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.xml.XmlTest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.nbc.support.ScreenshotManager;
import com.aventstack.extentreports.ExtentTest;
import com.nbc.support.Log;

public class Log {

	public static boolean printconsoleoutput = true;
	private static String screenShotFolderPath;
	private static String securityHTMLFolderPath;
	private static String testNgReport;
	private static String klovReport;
	private static AtomicInteger screenShotIndex = new AtomicInteger(0);
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
	private static HashMap<Integer, String> tests = new HashMap<Integer, String>();

	static String TEST_TITLE_HTML_BEGIN = null;
	static String TEST_TITLE_HTML_END = null;

	static String TEST_COND_HTML_BEGIN = null;
	static String TEST_COND_HTML_END = null;

	static String MESSAGE_HTML_BEGIN = null;
	static String MESSAGE_HTML_END = null;

	static String SECURITY_MESSAGE_HTML_BEGIN = null;
	static String SECURITY_MESSAGE_HTML_END = null;

	static String PASS_HTML_BEGIN = null;
	static String PASS_HTML_END1 = null;
	static String PASS_HTML_END2 = null;

	static String FAIL_HTML_BEGIN = null;
	static String FAIL_HTML_END1 = null;
	static String FAIL_HTML_END2 = null;

	static String SKIP_EXCEPTION_HTML_BEGIN = null;
	static String SKIP_HTML_END1 = null;
	static String SKIP_HTML_END2 = null;

	static String EVENT_HTML_BEGIN = null;
	static String EVENT_HTML_END = null;

	static String TRACE_HTML_BEGIN = null;
	static String TRACE_HTML_END = null;

	/**
	 * Static block clears the screenshot folder if any in the output during every suite execution and also sets up the print console flag for the run
	 */
	static {

		XmlTest xmlParameters = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		testNgReport = (xmlParameters.getParameter("testNgReport") != null) ? xmlParameters.getParameter("testNgReport") : "Yes";
		klovReport = (xmlParameters.getParameter("klovReport") != null) ? xmlParameters.getParameter("klovReport") : "Yes";

		if(testNgReport.equalsIgnoreCase("YES"))
		{
			TEST_TITLE_HTML_BEGIN = "&emsp;<div class=\"test-title\" align=\"center\"> <strong><font color = \"blue\">";
			TEST_TITLE_HTML_END = "</font> </strong> </div>&emsp;<div><strong>Steps:</strong></div>";

			TEST_COND_HTML_BEGIN = "&emsp;<div class=\"test-title\"> <strong><font size = \"3\" color = \"#0000FF\">";
			TEST_COND_HTML_END = "</font> </strong> </div>&emsp;";

			MESSAGE_HTML_BEGIN = "<div class=\"test-message\">&emsp;";
			MESSAGE_HTML_END = "</div>";

			SECURITY_MESSAGE_HTML_BEGIN = "<br> <div class=\"test-security-message\">&emsp;<strong><font size = \"3\" color = \"#FF4500\">";
			SECURITY_MESSAGE_HTML_END = "</font> </strong> </div> <br>";

			PASS_HTML_BEGIN = "<div class=\"test-result\"><br><font color=\"green\"><strong> ";
			PASS_HTML_END1 = " </strong></font> ";
			PASS_HTML_END2 = "</div>&emsp;";

			FAIL_HTML_BEGIN = "<div class=\"test-result\"><br><font color=\"red\"><strong> ";
			FAIL_HTML_END1 = " </strong></font> ";
			FAIL_HTML_END2 = "</div>&emsp;";

			SKIP_EXCEPTION_HTML_BEGIN = "<div class=\"test-result\"><br><font color=\"orange\"><strong> ";
			SKIP_HTML_END1 = " </strong></font> ";
			SKIP_HTML_END2 = " </strong></font> ";

			EVENT_HTML_BEGIN = "<div class=\"test-event\"> <font color=\"maroon\"> <small> &emsp; &emsp;--- ";
			EVENT_HTML_END = "</small> </font> </div>";

			TRACE_HTML_BEGIN = "<div class=\"test-event\"> <font color=\"brown\"> <small> &emsp; &emsp;--- ";
			TRACE_HTML_END = "</small> </font> </div>";
		}
		PropertyConfigurator.configure("./src/main/resources/log4j.properties");
		File screenShotFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
		screenShotFolderPath = screenShotFolder.getParent() + File.separator + "ScreenShot" + File.separator;
		screenShotFolder = new File(screenShotFolderPath);

		if (!screenShotFolder.exists()) {
			screenShotFolder.mkdir();
		}

		File[] screenShots = screenShotFolder.listFiles();

		// delete files if the folder has any
		if (screenShots != null && screenShots.length > 0) {
			for (File screenShot : screenShots) {
				screenShot.delete();
			}
		}

		File securityHTML = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
		securityHTMLFolderPath = securityHTML.getParent() + File.separator + "Security Scan Report" + File.separator;
		securityHTML = new File(securityHTMLFolderPath);

		if (!securityHTML.exists()) {
			securityHTML.mkdir();
		}

		File[] securityHtml = securityHTML.listFiles();

		// delete files if the folder has any
		if (securityHtml != null && securityHtml.length > 0) {
			for (File secHTML : securityHtml) {
				secHTML.delete();
			}
		}

		final Map <String, String> params = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getAllParameters();

		if (params.containsKey("printconsoleoutput")) {
			Log.printconsoleoutput = Boolean.parseBoolean(params.get("printconsoleoutput"));
		}

	} // static block
	/**
	 * cleanScreenShotFolder : Cleans screenshot folder of previous executions
	 * @param context
	 * @return None
	 * @throws Exception 
	 */
	public static void cleanScreenShotFolder(ITestContext context) throws Exception {

		try {

			printconsoleoutput = Boolean.valueOf(context.getCurrentXmlTest().getParameter("printconsoleoutput"));
			File screenShotFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
			screenShotFolder = new File(screenShotFolder.getParent() + File.separator + "ScreenShot");

			if (!screenShotFolder.exists()) {
				screenShotFolder.mkdir();
				return;
			}

			File[] screenShots = screenShotFolder.listFiles();

			for (File screenShot : screenShots)
				screenShot.delete();

			screenShotFolder = new File(screenShotFolder.getParent() + File.separator + "Pass_ScreenShot");

			if (!screenShotFolder.exists()) {
				screenShotFolder.mkdir();
				return;
			}

			screenShots = screenShotFolder.listFiles();

			for (File screenShot : screenShots)
				screenShot.delete();
		} //End try

		catch (Exception e) {
			throw new Exception ("Exception in Log.cleanScreenShotFolder;: "+e.getMessage(), e);
		} //End catch

	} //End cleanScreenShotFolder

	/**
	 * deleteDownloadedFilesFolder : Deletes download file folder
	 * @param context
	 * @return None
	 * @throws Exception 
	 */
	public static void deleteDownloadedFilesFolder(ITestContext context) throws Exception {

		try {

			File downloadedFilesFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
			downloadedFilesFolder = new File(downloadedFilesFolder.getParent() + File.separator + "DownloadedFiles");

			if (!downloadedFilesFolder.exists()) {
				downloadedFilesFolder.mkdir();
				return;
			}

			File[] downloadedFiles = downloadedFilesFolder.listFiles();

			for (File downloadedFile : downloadedFiles)
				downloadedFile.delete();
		} //End try
		catch (Exception e) {
			throw new Exception("Exception at Log.deleteDownloadedFilesFolder: "+e.getMessage(), e);
		} //End catch

	} //End deleteDownloadedFilesFolder


	/**
	 * event print the page object custom message in the log which can be seen
	 * through short cut keys used during debugging (level=info)
	 * 
	 * @param description
	 *            test case
	 */
	public static void event(String description) {

		if(testNgReport.equalsIgnoreCase("YES"))
		{
			String currDate = new SimpleDateFormat("dd MMM HH:mm:ss SSS").format(Calendar.getInstance().getTime());
			Reporter.log(EVENT_HTML_BEGIN + currDate + " - " + description + EVENT_HTML_END);
			lsLog4j().log(callerClass(), Level.DEBUG, description, null);
		}
	}

	/**
	 * Overrided with Extent Reports
	 * 
	 * @param extentedReport
	 * @throws Exception 
	 */
	public static void testCaseResultExtentReport(ExtentTest extentedReport, WebDriver driver) throws Exception {
		if (Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("FAILSOFT")) {
			Log.fail("Test Failed. Check the steps above in red color.", driver);
			fail("Test Failed. Check the steps above in red color.");
		} else {
			Log.pass("Test Passed.", driver);
			pass("Test Passed. ");
		}
		//endTest(extentedReport);
		endTestCase();
	}

	/**
	 * Overrided with Extent Reports
	 * 
	 * @param extentedReport
	 * @throws Exception 
	 */
	public static void testCaseResultExtentReport(WebDriver driver) throws Exception {
		if (Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("FAILSOFT")) {
			fail("Test Failed. Check the steps above in red color.", driver);
			//fail("Test Failed. Check the steps above in red color.");
		} else {
			pass("Test Passed.", driver);
			//pass("Test Passed. ");
		}
		//endTest(extentedReport);
		endTestCase();
	}

	/**
	 * event print the page object custom message in the log which can be seen
	 * through short cut keys used during debugging along with duration of the
	 * particular action (level=debug)
	 * 
	 * @param description
	 *            test case
	 * @param duration
	 *            to print the time taken
	 */
	public static void event(String description, long duration) {

		if(testNgReport.equalsIgnoreCase("YES"))
		{
			String currDate = new SimpleDateFormat("dd MMM HH:mm:ss SSS").format(Calendar.getInstance().getTime());
			Reporter.log(EVENT_HTML_BEGIN + currDate + " - <b>" + duration + "</b> - " + description + " - " + Thread.currentThread().getStackTrace()[2].toString() + EVENT_HTML_END);
			lsLog4j().log(callerClass(), Level.DEBUG, description+".."+duration, null);
		}
	}

	/**
	 * message print the test case security test message in the log with screenshot (level=info)
	 * 
	 * @param description
	 *            : test case
	 * @param driver
	 *            : to take screenshot
	 * @param inputFile
	 *            : HTML file name
	 */
	public static void securityMessage(String description, WebDriver driver, String inputFile) {

		if(testNgReport.equalsIgnoreCase("YES"))
			Reporter.log(SECURITY_MESSAGE_HTML_BEGIN + description + "&emsp;" + getSecurityScanHTMLHyperLink(inputFile + ".html") + SECURITY_MESSAGE_HTML_END);

		lsLog4j().log(callerClass(), Level.INFO, description, null);


	}

	/**
	 * getSecurityScanHTMLHyperLink will convert the log status to hyper link
	 * @param inputFile
	 *            converts log status to hyper link
	 * 
	 * @return String take Security Scan HTML link path
	 */
	public static String getSecurityScanHTMLHyperLink(String inputFile) {
		String screenShotLink = "";
		screenShotLink = "<a href=\"." + File.separator + "Security Scan Report" + File.separator + inputFile + "\" target=\"_blank\" >[HTML Scan Report]</a>";
		return screenShotLink;
	}

	/** * warning :  Prints Warning description messages
	 * 
	 */
	public static void warning( String description) {

		if(testNgReport.equalsIgnoreCase("YES"))
			Reporter.log("<br><font color=\"Orange\"><strong>" + description + "</strong></font></br>");

		lsLog4j().log(callerClass(), Level.WARN, description, null );
		ExtentReporter.warning(description);
	}

	/**
	 * message print the test case custom message in the log (level=info)
	 * 
	 * @param description
	 *            test case
	 */
	public static void message(String description) {

		if(testNgReport.equalsIgnoreCase("YES"))
			Reporter.log(MESSAGE_HTML_BEGIN + description + MESSAGE_HTML_END);

		lsLog4j().log(callerClass(), Level.INFO, description, null);
		ExtentReporter.info(description);
	}

	/**
	 * message : Prints the messages
	 * @param  description - Message to be printed
	 * @param  driver - Web Driver
	 * @return None
	 * @throws Exception 
	 */
	public static void message(String description, WebDriver driver) throws Exception {

		String inputFile = "";

		try{

			inputFile = takeScreenShot(driver);
			if(testNgReport.equalsIgnoreCase("YES"))
				Reporter.log(MESSAGE_HTML_BEGIN + description + "&emsp;" + getScreenShotHyperLink(inputFile) + MESSAGE_HTML_END);

			lsLog4j().log(callerClass(), Level.INFO, description, null);

			if(klovReport.equalsIgnoreCase("No"))
				ExtentReporter.info(description, "./ScreenShot/"+ inputFile);
			else
				ExtentReporter.info(description, screenShotFolderPath + inputFile);
				//ExtentReporter.infoBase64(description, "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

		}
		catch (Exception e) {
			throw new Exception ("Exception at Log.pass;: "+e.getMessage(), e);
		} //End catch
	}

	/**
	 * pass : Prints the pass description messages
	 * @param  msg - Message to be printed
	 * @return None
	 * @throws Exception 
	 */
	public static void pass(String msg) throws Exception {

		try {

			if(testNgReport.equalsIgnoreCase("YES"))
				Reporter.log("<br><font color=\"green\"><strong>" + msg + "</strong></font></br>");

			if (printconsoleoutput)
				System.out.println(msg);
			lsLog4j().log(callerClass(), Level.INFO, msg, null);
			ExtentReporter.pass(msg);
		} //End try

		catch (Exception e) {
			throw new Exception ("Exception at Log.pass;: "+e.getMessage(), e);
		} //End catch
	} //End pass


	/**
	 * pass : Prints the pass description messages
	 * @param  msg - Message to be printed
	 * @return None
	 * @throws Exception 
	 */
	public static void pass(String msg, WebDriver driver) throws Exception {

		try {

			String inputFile = "";

			try {
				File screenShotFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
				String strBasePath = screenShotFolder.getParent() + File.separator + "ScreenShot" + File.separator;
				inputFile = Reporter.getCurrentTestResult().getName() + "_" + (new Date()).getTime() + ".png";
				driver.switchTo().defaultContent();
				WebDriver augmented = new Augmenter().augment(driver);
				File screenshot = ((TakesScreenshot) augmented).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshot, new File(strBasePath + inputFile));
			}
			catch (IOException | WebDriverException e1) {
				try {
					message("Screen shot capture failed.");
				} catch (Exception e) {
					e.printStackTrace();
					ExtentReporter.logStackTrace(e);
				}
				e1.printStackTrace();
				ExtentReporter.logStackTrace(e1);
			}
			if(testNgReport.equalsIgnoreCase("YES"))
			{
				String screenShotLink = "<a href=\"." + File.separator + "ScreenShot" + File.separator + inputFile + "\" target=\"_blank\" >[ScreenShot]</a>";
				Reporter.log("<br><font color=\"green\"><strong>" + msg + "</strong></font></br><p>" + screenShotLink + "</p>");
			}

			if(printconsoleoutput) 
				System.out.println(msg);

			lsLog4j().log(callerClass(), Level.INFO, msg, null);

			if(klovReport.equalsIgnoreCase("No"))
				ExtentReporter.pass(msg, "./ScreenShot/"+ inputFile);
			else
				ExtentReporter.pass(msg, screenShotFolderPath + inputFile);
				//ExtentReporter.passBase64(msg, "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
		} //End try

		catch (Exception e) {
			throw new Exception ("Exception at Log.pass;: "+e.getMessage(), e);
		} //End catch

	} //End pass


	/**
	 * fail print test case status as Fail with custom message (level=error)
	 * 
	 * @param description
	 *            custom message in the test case
	 */
	public static void fail(String description) {
		if(testNgReport.equalsIgnoreCase("YES"))
			Reporter.log(FAIL_HTML_BEGIN + description + FAIL_HTML_END1 + FAIL_HTML_END2);
		lsLog4j().log(callerClass(), Level.ERROR, description, null);
		ExtentReporter.fail(description);
		Assert.fail(description);
	}



	/**
	 * fail : Prints the fail description messages
	 * @param  msg - Message to be printed
	 * @param  driver - Web driver
	 * @return None
	 * @throws Exception 
	 */
	public static void fail(String msg, WebDriver driver) throws Exception {

		String inputFile = "";

		try {
			File screenShotFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
			String strBasePath = screenShotFolder.getParent() + File.separator + "ScreenShot" + File.separator;
			inputFile = Reporter.getCurrentTestResult().getName() + "_" + (new Date()).getTime() + ".png";
			driver.switchTo().defaultContent();
			WebDriver augmented = new Augmenter().augment(driver);
			File screenshot = ((TakesScreenshot) augmented).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(strBasePath + inputFile));
		}
		catch (IOException | WebDriverException e1) {
			try {
				message("Screen shot capture failed.");
			} catch (Exception e) {
				e.printStackTrace();
				ExtentReporter.logStackTrace(e);
			}
			e1.printStackTrace();
			ExtentReporter.logStackTrace(e1);
		}

		lsLog4j().log(callerClass(), Level.FATAL, msg, null);

		if(klovReport.equalsIgnoreCase("No"))
			ExtentReporter.fail(msg, "./ScreenShot/"+ inputFile);
		else
			ExtentReporter.fail(msg, screenShotFolderPath+inputFile);
			//ExtentReporter.failBase64(msg, "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));

		if(testNgReport.equalsIgnoreCase("YES"))
		{
			String screenShotLink = "<a href=\"." + File.separator + "ScreenShot" + File.separator + inputFile + "\" target=\"_blank\" >[ScreenShot]</a>";
			Reporter.log("<br><font color=\"red\"><strong>"+msg+"</strong></font></br><p>" + screenShotLink + "</p>");
		}


		if (printconsoleoutput)
			System.out.println("Fail: " + msg);
		Assert.fail(msg);
	} //End fail


	public static void exception(Exception e) throws Exception{

		String eMessage = e.getMessage();
		if (eMessage != null && eMessage.contains("\n")) {
			eMessage = eMessage.substring(0, eMessage.indexOf("\n"));
		}

		if (e instanceof SkipException) {
			if(testNgReport.equalsIgnoreCase("YES"))
				Reporter.log(SKIP_EXCEPTION_HTML_BEGIN + eMessage + SKIP_HTML_END1 + SKIP_HTML_END2);
			ExtentReporter.skip(eMessage);
			ExtentReporter.logStackTrace(e);
		}
		else {
			if(testNgReport.equalsIgnoreCase("YES"))
				Reporter.log("<br><div class=\"exceptions\"> <font color=\"red\"><I>" + e.getMessage() + " </I> </font> </div></br>");
			ExtentReporter.error(eMessage);
			ExtentReporter.logStackTrace(e);
		}
		throw e;

	}


	/**
	 * exception : Prints the exception description messages
	 * @param  e - Exception details
	 * @param  driver - Web Driver
	 * @param  extentTest - Extent Test
	 * @return None
	 * @throws Exception 
	 */
	public static void exception(Exception e, WebDriver driver) throws Exception {

		if (driver != null) {

			String inputFile = "";

			try
			{
				File screenShotFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
				String strBasePath = screenShotFolder.getParent() + File.separator + "ScreenShot" + File.separator;
				inputFile = Reporter.getCurrentTestResult().getName() + "_" + (new Date()).getTime() + ".png";
				driver.switchTo().defaultContent();
				WebDriver augmented = new Augmenter().augment(driver);
				File screenshot = ((TakesScreenshot) augmented).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshot, new File(strBasePath + inputFile));
			}
			catch (IOException | WebDriverException e1) {
				try {
					message("Screen shot capture failed.");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				e1.printStackTrace();
			}
			String eMessage = e.getMessage().replaceAll("<", "{").replaceAll(">", "}").trim();

			if (eMessage != null && eMessage.contains("\n")) {
				eMessage = eMessage.substring(0, eMessage.indexOf("\n"));
			}

			lsLog4j().log(callerClass(), Level.FATAL, eMessage, e);

			if (e instanceof SkipException) {
				if(testNgReport.equalsIgnoreCase("YES"))
					Reporter.log(SKIP_EXCEPTION_HTML_BEGIN + eMessage + SKIP_HTML_END1 + getScreenShotHyperLink(inputFile) + SKIP_HTML_END2);
				if(klovReport.equalsIgnoreCase("No"))
					ExtentReporter.skip(eMessage, "./ScreenShot/"+ inputFile);
				else
					ExtentReporter.skip(eMessage, screenShotFolderPath + inputFile);
					//ExtentReporter.skipBase64(eMessage, "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
				ExtentReporter.logStackTrace(e);
			}
			else {
				if(testNgReport.equalsIgnoreCase("YES"))
					Reporter.log(FAIL_HTML_BEGIN + eMessage + FAIL_HTML_END1 + getScreenShotHyperLink(inputFile) + FAIL_HTML_END2);
				try
				{
					if(klovReport.equalsIgnoreCase("No"))
						ExtentReporter.error(eMessage, "./ScreenShot/"+ inputFile);
					else
						ExtentReporter.error(eMessage, screenShotFolderPath + inputFile);
						//ExtentReporter.errorBase64(eMessage, "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64));
				}catch(Exception e0){}
				ExtentReporter.logStackTrace(e);
			}
			throw e;
		}
		else {
			Log.exception(e);
		}
	}


	/**
	 * addTestRunMachineInfo : Prints the machine information messages
	 * @param  driver - Web driver
	 * @param  context - ITestContext
	 * @return None
	 * @throws Exception 
	 */
	public static void addTestRunMachineInfo(WebDriver driver) throws Exception {
		try {

			Object params[] = Reporter.getCurrentTestResult().getParameters();

			String testMachine = "";
			String machineInfo = "";
			String browser = getBrowserName(driver);
			String osUsed = System.getProperty("os.name");
			String javaVersion = System.getProperty("java.version");
			String hub = (Reporter.getCurrentTestResult().getHost() == null) ? Inet4Address.getLocalHost().getHostName() : Reporter.getCurrentTestResult().getHost();
			testMachine = "Hub: " + hub + "; Node: " + WebDriverFactory.getTestSessionNodeIP(driver).toUpperCase() + "; " +
					"Browser: " +	browser +"; Java Version: " + javaVersion + "; Operating System: " + osUsed + "; ";

			try {
				String testDataCount= "";
				if(params.length > 0)
					if(params[0].toString().contains("<script>") || params[0].toString().contains("iframe"))
					{
						params[0] = params[0].toString().replaceAll("<", "{");
						params[0] = params[0].toString().replaceAll(">", "}");
						params[0] += "(NOTE: For Script/iFrame values '<' is replaced with '{' and '>' is replaced with '}')";
					}

				if (params.length > 0) {
					if (params[0].toString().contains("S.No="))
						testDataCount = params[0].toString().split("S.No=")[1].split(",")[0];

					params[0] = "Machine Details -" + testMachine + ";Test Data -" + testDataCount + ": " + params[0].toString().trim();
				}
				else {
					ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(params));
					temp.add(testMachine);
					temp.add(browser.split(" ")[0]);
					params = temp.toArray();
				}				
				machineInfo += "<b>Machine info.</b> - " + testMachine + ";<br>";
				if(params[0].toString().trim().contains("Test Data"))
					machineInfo += "<b>Test Data</b> - " + params[0].toString().trim().split("Test Data")[1];
			}
			catch (Exception e1) {} 
			Reporter.getCurrentTestResult().setParameters(params);
			if(!machineInfo.trim().equals(""))
				ExtentReporter.testMachineInfo(machineInfo);

		} //End try

		catch (Exception e) {
			//throw new SkipException("Exception at Log.addTestRunMachineInfo: "+e.getMessage(), e);
		} //End catch

	} //End addTestRunMachineInfo

	/**
	 * addTestRunMachineInfo : Prints the machine information messages
	 * @param  driver - Web driver
	 * @param  context - ITestContext
	 * @return None
	 * @throws Exception 
	 */
	public static void addTestRunMachineInfo(WebDriver driver, String driverType, ITestContext context) throws Exception {
		try {

			Object params[] = Reporter.getCurrentTestResult().getParameters();
			String testMachine = "";
			String hub = "localhost";
			String browser = driverType + "-" +context.getCurrentXmlTest().getParameter("driverVersion");
			String osUsed = System.getProperty("os.name");
			String javaVersion = System.getProperty("java.version");
			String applBuild = context.getCurrentXmlTest().getParameter("webAccessSprint");
			String MfilesVerion = context.getCurrentXmlTest().getParameter("MFilesAPI");
			hub = (Reporter.getCurrentTestResult().getHost() == null) ? Inet4Address.getLocalHost().getHostName() : Reporter.getCurrentTestResult().getHost();
			testMachine = "(Hub: " + hub + ", Node: " + WebDriverFactory.getTestSessionNodeIP(driver).toUpperCase() + "), Browser: "+
					browser+", Java Version: "+javaVersion+" Operating System: "+osUsed+" Application Build: "+applBuild+" MFilesVersion: "+MfilesVerion;
			params[0] = testMachine + ", Test Data : " + params[0].toString().trim();

			Reporter.getCurrentTestResult().setParameters(params);

		} //End try

		catch (Exception e) {
			throw new Exception("Exception at Log.addTestRunMachineInfo: "+e.getMessage(), e);
		} //End catch

	} //End addTestRunMachineInfo

	/**
	 * pass print test case status as Pass with custom message (level=info)
	 * 
	 * @param description
	 *            custom message in the test case
	 * @param extentedReport
	 *            Extent Report
	 * @param driver
	 *            Webdriver
	 */
	public static void passExtentReport(String description, ExtentTest extentedReport, WebDriver driver) {
		String inputFile = takeScreenShot(driver);
		if (configProperty.getProperty("isTakeScreenShot") != null
				&& configProperty.getProperty("isTakeScreenShot").equalsIgnoreCase("true")) {
			Reporter.log(PASS_HTML_BEGIN + description + PASS_HTML_END1 + getScreenShotHyperLink(inputFile)
			+ PASS_HTML_END2);
		} else {
			Reporter.log(PASS_HTML_BEGIN + description + PASS_HTML_END1 + PASS_HTML_END2);
		}
		lsLog4j().log(callerClass(), Level.INFO, description, null);
		//extentedReport.log(LogStatus.PASS, description + extentedReport.addScreenCapture("./ScreenShot/" + inputFile));
	}


	/**
	 * testCaseInfo : Prints the test case information
	 * @param  description - test case description
	 * @return None
	 * @throws Exception 
	 */
	public static void testCaseInfo(String description) throws Exception {

		try {

			if (Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("<div class=\"test-title\">"))
				Reporter.log("<div class=\"test-title\"> <strong><font color = \"blue\"> Description : " + description + "</font> </strong> </div><div><strong>Steps:</strong></div>");
			else 
				Reporter.log("<div class=\"test-title\"> <strong><font color = \"blue\"> Description : " + description + "</font> </strong> </div><div><strong>Steps:</strong></div><!-- Report -->");

			if (printconsoleoutput)
				System.out.println(description);

		} //End try

		catch (Exception e) {
			throw new Exception ("Exception at Log.testCaseInfo;: "+e.getMessage(), e);
		} //End catch

	} //End testCaseInfo

	/**
	 * testCaseInfo : Prints the test case information
	 * @param  description - test case description
	 * @return 
	 * @return None
	 * @throws Exception 
	 */
	public static void testCaseInfo(String description, String testName, String category, String author, WebDriver driver) throws Exception {

		int hashCode = Reporter.getCurrentTestResult().hashCode();

		if(!tests.containsKey(hashCode))//Checks if the info is already logged for the test
		{
			tests.put(hashCode, testName);
			lsLog4j().setLevel(Level.ALL);
			lsLog4j().info("");
			lsLog4j().log(callerClass(), Level.INFO, "****             " + description + "             *****", null);
			lsLog4j().info("");

			if(testNgReport.equalsIgnoreCase("YES"))
				if (Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("<div class=\"test-title\">")) {
					Reporter.log(TEST_TITLE_HTML_BEGIN + description + TEST_TITLE_HTML_END);
				}
				else {
					Reporter.log(TEST_TITLE_HTML_BEGIN + description + TEST_TITLE_HTML_END + "<!-- Report -->");
				}
			ExtentReporter.testCaseInfo(description, testName);

			addTestRunMachineInfo(driver);			
		}

	}

	/**
	 * fail print test case status as Fail with custom message and take screenshot
	 * (level=error)
	 * 
	 * @param description
	 *            custom message in the test case
	 * @param driver
	 *            to take screenshot
	 */
	public static void failExtentReport(String description, ExtentTest extentedReport, WebDriver driver) {
		String inputFile = takeScreenShot(driver);
		Reporter.log(
				FAIL_HTML_BEGIN + description + FAIL_HTML_END1 + getScreenShotHyperLink(inputFile) + FAIL_HTML_END2);
		lsLog4j().log(callerClass(), Level.ERROR, description, null);
		//extentedReport.log(LogStatus.FAIL, description + extentedReport.addScreenCapture("./ScreenShot/" + inputFile));
		Assert.fail(description);		
	}


	/**
	 * testCaseInfo : Prints the test case information
	 * @param  None
	 * @return None
	 * @throws Exception 
	 */
	public static void testCaseInfo() throws Exception {

		try {

			String description = Reporter.getCurrentTestResult().getMethod().getDescription().toString().trim();

			if (Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("<div class=\"test-title\">"))
				Reporter.log("<div class=\"test-title\" align=\"center\"> <strong><font color = \"blue\">" + description + "</font> </strong> </div></br><div><strong>Steps:</strong></div>");
			else 
				Reporter.log("<div class=\"test-title\" align=\"center\"> <strong><font color = \"blue\">" + description + "</font> </strong> </div></br><div><strong>Steps:</strong></div><!-- Report -->");

			if (printconsoleoutput)
				System.out.println(description);

		} //End try

		catch (Exception e) {
			throw new Exception ("Exception at Log.testCaseInfo;: "+e.getMessage(), e);
		} //End catch

	} //End testCaseInfo

	/**
	 * testDataInfo : Prints the test data information
	 * @param  message - test data description
	 * @return None
	 * @throws Exception 
	 */
	public static void testDataInfo(String message) throws Exception {

		try {

			Reporter.log("<strong><font color = \"blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + message + "</font> </strong><br>");

			if (printconsoleoutput)
				System.out.println(message);
		} //End try

		catch (Exception e) {
			throw new Exception("Exception at Log.testDataInfo;: "+e.getMessage(), e);
		} //End catch

	} //End testDataInfo

	/**
	 * getStackTrace : Gets stack trace
	 * @param  throwable
	 * @return String
	 * @throws Exception 
	 */
	public String getStackTrace(final Throwable throwable) throws Exception {
		try {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw, true);
			throwable.printStackTrace(pw);
			return sw.getBuffer().toString();
		}
		catch (Exception e) {
			throw new Exception("Exception at getStackTrace : " + e);
		}
	}

	/**
	 * getScreenShotHyperLink will convert the log status to hyper link
	 * 
	 * @param inputFile
	 *            converts log status to hyper link
	 * 
	 * @return String take sheet shot link path
	 */
	public static String getScreenShotHyperLink(String inputFile) {
		String screenShotLink = "";
		screenShotLink = "<a href=\"." + File.separator + "ScreenShot" + File.separator + inputFile + "\" target=\"_blank\" >[ScreenShot]</a>";
		return screenShotLink;
	}	

	/**
	 * endTestCase : Ends test case
	 * @param  extentTest
	 * @return None
	 * @throws Exception 
	 */
	public static void endTestCase() throws Exception {
		try {
			lsLog4j().info("****             " + "-E---N---D-" + "             *****");
			ExtentReporter.endTest();
		}
		catch (Exception e) {
			throw new Exception("Exception at endTestCase : " + e);
		}

	} //endTestCase

	/**
	 * lsLog4j returns name of the logger from the current thread
	 * 
	 * @return get current thread name
	 */
	public static Logger lsLog4j() {
		return Logger.getLogger(Thread.currentThread().getName());
	}

	/**
	 * callerClass method used to retrieve the Class Name
	 * 
	 * @return String -current test class name
	 */
	public static String callerClass() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

	/**
	 * takeScreenShot will take the screenshot by sending driver as parameter in the log and puts in the screenshot folder
	 * 
	 * @param driver
	 *            to take screenshot
	 * @return String take sheet shot path
	 */
	public static String takeScreenShot(WebDriver driver) {
		String inputFile = "";
		inputFile = Reporter.getCurrentTestResult().getName() + "_" + screenShotIndex.incrementAndGet() + ".png";
		ScreenshotManager.takeScreenshot(driver, screenShotFolderPath + inputFile);
		return inputFile;
	}

	/** 
	 * getBrowserName : This method is to get the name of the current executing browser
	 * @param driver
	 * @return Browser name with version
	 * @throws Exception
	 */
	public static String getBrowserName(WebDriver driver) throws Exception {

		//Variable Declaration
		String browserName = "";

		try {

			Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
			browserName = cap.getBrowserName();
			String browser_version = "";

			if (browserName.equalsIgnoreCase("INTERNET EXPLORER")) {
				String uAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");

				if (uAgent.contains("MSIE") && uAgent.contains("Windows")) 
					browser_version = uAgent.substring(uAgent.indexOf("MSIE") + 5, uAgent.indexOf("Windows") - 2);
				else if (uAgent.contains("Trident/7.0"))
					browser_version = "11.0";
				else
					browser_version = "0.0";

			}
			else
			{
				try{
					browser_version = cap.getCapability("browserVersion").toString();				
				}
				catch(Exception e0){browser_version = cap.getVersion();}
			}

			if (!browser_version.equals(""))
				browserName =  browserName + " " + browser_version.substring(0, browser_version.indexOf("."));

		} //End try

		catch (Exception e) {
			throw new Exception ("Exception at Utility.getBrowserName : "+e.getMessage(), e);
		} //End catch	

		return browserName;

	} //End getBrowserName

	/**
	 * Asserts that a condition is true or false, depends upon the status. Then it
	 * will print the verified message if status is true, else stop the script and
	 * print the failed message
	 * 
	 * @param status
	 *            - boolean or expression returning boolean
	 * @param passmsg
	 *            -message to be logged when assert status is true
	 * @param failmsg
	 *            -message to be logged when assert status is false
	 */
	public static void assertThat(boolean status, String passmsg, String failmsg) {
		if (!status) {
			Log.fail(failmsg);
		} else {
			Log.message(passmsg);
		}
	}

	/**
	 * Asserts that a condition is true or false, depends upon the status. Then it
	 * will print the verified message with screen shot if status is true, else stop
	 * the script and print the failed message with screen shot
	 * 
	 * @param status
	 *            - boolean or expression returning boolean
	 * @param passmsg
	 *            -message to be logged when assert status is true
	 * @param failmsg
	 *            -message to be logged when assert status is false
	 * @param driver
	 *            - WebDriver, using this driver will taking the screen shot and
	 *            mapping to log report
	 * @throws Exception 
	 */
	public static void assertThat(boolean status, String passmsg, String failmsg, WebDriver driver) throws Exception {
		if (!status) {
			Log.fail(failmsg, driver);
		} else {
			Log.message(passmsg, driver);
		}
	}

	public static void assertThatExtentReport(boolean status, String passmsg, String failmsg, ExtentTest extentedReport,
			WebDriver driver) {
		if (!status) {
			Log.failExtentReport(failmsg, extentedReport, driver);
		} else {
			Log.passExtentReport(passmsg, extentedReport, driver);
		}
	}

	public static void assertThatExtentReport(boolean status, String passmsg, String failmsg,
			WebDriver driver) throws Exception {
		if (!status) {
			Log.fail(failmsg, driver);
		} else {
			Log.pass(passmsg, driver);
		}
	}

	public static void assertThatForEvent(boolean status, String passmsg, String failmsg, long duration, WebDriver driver) throws Exception {
		if (!status) {
			Log.fail(failmsg, driver);
		} else {
			Log.event(passmsg, duration);
		}
	}

	/**
	 * Asserts that a condition is true or false, depends upon the status. Then it
	 * will print the verified message if status is true, else print the failed
	 * message in red color and continue the next step(not stopping/breaking the
	 * test script)
	 * 
	 * @param status
	 *            - boolean or expression returning boolean
	 * @param passmsg
	 *            -message to be logged when assert status is true
	 * @param failmsg
	 *            -message to be logged when assert status is false
	 */
	public static void softAssertThat(boolean status, String passmsg, String failmsg) {
		if (!status) {
			Log.failsoft(failmsg);
		} else {
			Log.message(passmsg);
		}
	}

	/**
	 * Asserts that a condition is true or false, depends upon the status. Then it
	 * will print the verified message with screen shot if status is true, else
	 * print the failed message in red color with screen shot and continue the next
	 * step(not stopping/breaking the test script)
	 * 
	 * @param status
	 *            - boolean or expression returning boolean
	 * @param passmsg
	 *            -message to be logged when assert status is true
	 * @param failmsg
	 *            -message to be logged when assert status is false
	 * @param driver
	 *            - WebDriver, using this driver will taking the screen shot and
	 *            mapping to log report
	 * @throws Exception 
	 */
	public static void softAssertThat(boolean status, String passmsg, String failmsg, WebDriver driver) throws Exception {
		if (!status) {
			Log.failsoft(failmsg, driver);
		} else {
			Log.message(passmsg, driver);
		}
	}

	/**
	 * hasFailSofts returns true if the test steps contains any fail
	 * 
	 * @return boolean return true if test have fail soft, else fail
	 */
	public static boolean hasFailSofts() {
		return Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("FAILSOFT");
	}

	/**
	 * failsoft print test case step failure as fail with screenshot and let
	 * execution continue (level=error)
	 * 
	 * @param description
	 *            custom message in the test case
	 * @param driver
	 *            to take screenshot
	 */
	public static void failsoft(String description, WebDriver driver) {

		String inputFile = takeScreenShot(driver);
		Reporter.log("<div class=\"test-result\"><font color=\"red\">&emsp;" + description + "</font>"
				+ getScreenShotHyperLink(inputFile) + "</div>");
		Reporter.log("<!--FAILSOFT-->");
		lsLog4j().log(callerClass(), Level.ERROR, description, null);

	}

	/**
	 * failsoft print test case step failure as fail and let execution continue
	 * (level=error)
	 * 
	 * @param description
	 *            custom message in the test case
	 */
	public static void failsoft(String description) {
		Reporter.log("<!--FAILSOFT-->");
		lsLog4j().log(callerClass(), Level.ERROR, description, null);
	}

	/**
	 * failsoft print test case step failure as fail in red color with screenshot
	 * (level=error)
	 * 
	 * @param description
	 *            custom message in the test case
	 * @param driver
	 *            to take screenshot
	 * @return String - build fail soft message
	 */
	public static String buildfailsoftMessage(String description, WebDriver driver) {

		String inputFile = takeScreenShot(driver);
		return "<div class=\"test-result\">&emsp; <font color=\"red\"><strong>" + description + " </strong> </font>"
		+ getScreenShotHyperLink(inputFile) + "</div>&emsp;";
	}
} //End Log
package com.nbc.support;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.xml.XmlTest;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;

/**
 * WebdriverFactory class used to get a web driver instance, depends on the user
 * requirement as driverHost, driverPort and browserName we adding the
 * desiredCapabilities and other static action initialized here and some methods
 * used to retrieve the Hub and node information. It also consists page wait
 * load for images/frames/document
 */

public class WebDriverFactory {
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
	public static BrowserMobProxy proxy;
	static Proxy seleniumProxy;
	static String driverHost;
	static String driverPort;
	static String browserName;
	static String deviceName;
	static URL hubURL;
	static Proxy zapProxy = new Proxy();

	public static String sFileName = "./NBC_.har";
	static DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
	static DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
	static DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
	static DesiredCapabilities safariCapabilities = DesiredCapabilities.safari();
	static DesiredCapabilities iOS_SimulatorCapabilities = new DesiredCapabilities();
	static DesiredCapabilities androidSimulatorCapabilities = new DesiredCapabilities();
	static DesiredCapabilities iOSDeviceCapabilities = new DesiredCapabilities();
	static ChromeOptions opt = new ChromeOptions();
	static FirefoxProfile fp = new FirefoxProfile();
	public static ExpectedCondition<Boolean> documentLoad;
	public static ExpectedCondition<Boolean> framesLoad;
	public static ExpectedCondition<Boolean> imagesLoad;
	public static int maxPageLoadWait = 90;

	public static String includeframesLoad;
	public static String includeimagesLoad;
	public static String includeDocumentLoad;
	final static String URL = "https://vasanthmanickam:94833836-2842-4a5c-af4f-3b920adf97b0@ondemand.saucelabs.com:443/wd/hub";

	static {
		try {
			documentLoad = new ExpectedCondition<Boolean>() {
				public final Boolean apply(final WebDriver driver) {
					final JavascriptExecutor js = (JavascriptExecutor) driver;
					boolean docReadyState = false;
					try {
						docReadyState = (Boolean) js.executeScript(
								"return (function() { if (document.readyState != 'complete') {  return false; } if (window.jQuery != null && window.jQuery != undefined && window.jQuery.active) { return false;} if (window.jQuery != null && window.jQuery != undefined && window.jQuery.ajax != null && window.jQuery.ajax != undefined && window.jQuery.ajax.active) {return false;}  if (window.angular != null && angular.element(document).injector() != null && angular.element(document).injector().get('$http').pendingRequests.length) return false; return true;})();");
					} catch (WebDriverException e) {
						docReadyState = true;
					}
					return docReadyState;

				}
			};

			imagesLoad = new ExpectedCondition<Boolean>() {
				public final Boolean apply(final WebDriver driver) {
					boolean docReadyState = true;
					try {
						JavascriptExecutor js;
						List<WebElement> images = driver.findElements(By.cssSelector("img[src]"));
						for (int i = 0; i < images.size(); i++) {
							try {
								js = (JavascriptExecutor) driver;
								docReadyState = docReadyState && (Boolean) js.executeScript(
										"return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
										images.get(i));
								if (!docReadyState) {
									break;
								}
							} catch (StaleElementReferenceException e) {
								images = driver.findElements(By.cssSelector("img[src]"));
								i--;
								continue;
							} catch (WebDriverException e) {

								// setting the true value if any exception arise
								// Ex:: inside frame or switching to new windows or
								// switching to new frames
								docReadyState = true;
							}
						}
					} catch (WebDriverException e) {
						docReadyState = true;
					}
					return docReadyState;
				}
			};

			framesLoad = new ExpectedCondition<Boolean>() {
				public final Boolean apply(final WebDriver driver) {
					boolean docReadyState = true;
					try {
						JavascriptExecutor js;
						List<WebElement> frames = driver.findElements(By.cssSelector("iframe[style*='hidden']"));
						for (WebElement frame : frames) {
							try {
								driver.switchTo().defaultContent();
								driver.switchTo().frame(frame);
								js = (JavascriptExecutor) driver;
								docReadyState = docReadyState
										&& (Boolean) js.executeScript("return (document.readyState==\"complete\")");
								driver.switchTo().defaultContent();
								if (!docReadyState) {
									break;
								}
							} catch (WebDriverException e) {
								docReadyState = true;
							}
						}
					} catch (WebDriverException e) {
						docReadyState = true;
					} finally {
						driver.switchTo().defaultContent();
					}
					return docReadyState;
				}
			};

			XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
			driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost")
					: test.getParameter("deviceHost");
			driverPort = System.getProperty("hubPort") != null ? System.getProperty("hubPort")
					: test.getParameter("devicePort");

			includeimagesLoad = test.getParameter("imageLoad");
			includeframesLoad = test.getParameter("framesLoad");
			includeDocumentLoad = test.getParameter("documentLoad");

			maxPageLoadWait = configProperty.getProperty("maxPageLoadWait") != null
					? Integer.valueOf(configProperty.getProperty("maxPageLoadWait"))
					: maxPageLoadWait;

			opt.addArguments("--ignore-certificate-errors");
			opt.addArguments("--disable-bundled-ppapi-flash");
			opt.addArguments("--disable-extensions");
			opt.addArguments("--disable-web-security");
			opt.addArguments("--always-authorize-plugins");
			opt.addArguments("--allow-running-insecure-content");
			opt.addArguments("--test-type");
			opt.addArguments("--enable-npapi");
			chromeCapabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

			try {
				hubURL = new URL("http://" + driverHost + ":" + driverPort + "/wd/hub");
			} catch (MalformedURLException e) {
				// e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to get instance of web driver using default parameters
	 * 
	 * @param xmlRead
	 *            : TestNG XML
	 * @return driver: WebDriver Instance
	 * @throws Exception 
	 */
	public static WebDriver get(boolean xmlRead) throws Exception {
		if (xmlRead)
			browserName = System.getProperty("browserName") != null ? System.getProperty("browserName")
					: Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browserName")
							.toLowerCase();
		else
			browserName = "chrome_windows";

		return get(browserName, xmlRead);
	}

	/**
	 * Webdriver to get the web driver with browser name and platform and setting
	 * the desired capabilities for browsers
	 * 
	 * @param browserWithPlatform
	 *            : Browser With Platform
	 * @param xmlRead
	 *            : Ream from TestNG XML
	 * @return driver: WebDriver Instance
	 * @throws Exception 
	 */
	public static WebDriver get(String browserWithPlatform, boolean xmlRead) throws Exception {

		WebDriver driver = null;
		String browser = null, platform = null;
		long startTime = StopWatch.startTime();

		if (System.getProperty("har") == "true") {
			// start the proxy
			proxy = new BrowserMobProxyServer();
			proxy.start(0);
		}

		// get the Selenium proxy object - org.openqa.selenium.Proxy;
		if (System.getProperty("har") == "true")
			seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

		if (browserWithPlatform.contains("_")) {
			browser = browserWithPlatform.split("_")[0].toLowerCase().trim();
			platform = browserWithPlatform.split("_")[1].toUpperCase().trim();
		} else {
			platform = "ANY";
		}

		try {
			if ("chrome".equalsIgnoreCase(browser)) {
				chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
				chromeCapabilities.setPlatform(Platform.fromString(platform));

				if (System.getProperty("har") == "true")
					chromeCapabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

				driver = new RemoteWebDriver(hubURL, chromeCapabilities);

			} else if ("chromewithuseragent".equalsIgnoreCase(browser)) {
				opt.addArguments(
						"--user-agent=Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10");
				// opt.addArguments("start-maximized");
				chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
				chromeCapabilities.setCapability("opera.arguments", "-screenwidth 1024 -screenheight 768");
				chromeCapabilities.setPlatform(Platform.fromString(platform));
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				chromeCapabilities.setCapability("name", methodName );
				if (System.getProperty("har") == "true")
					chromeCapabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

				driver = new RemoteWebDriver(hubURL, chromeCapabilities);

			} else if ("SauceChromeWithUserAgentiPad".equalsIgnoreCase(browser)) {
				opt.addArguments(
						"--user-agent=Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10");
				chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
				chromeCapabilities.setCapability("opera.arguments", "-screenwidth 1024 -screenheight 768");
				chromeCapabilities.setPlatform(Platform.fromString(platform));
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				chromeCapabilities.setCapability("name", methodName );
				System.out.println("methodd11111111111111111111"+methodName);
				driver = new RemoteWebDriver(new URL(URL), chromeCapabilities);

			} else if ("SauceChromeWithUserAgentPC".equalsIgnoreCase(browser)) {
				chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
				chromeCapabilities.setCapability("platform", "Windows 10");
				chromeCapabilities.setCapability("version", "64.0");
				chromeCapabilities.setCapability("opera.arguments", "-screenwidth 2560 -screenheight 1600");
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				System.out.println("methodd11111111111111111111"+methodName);
				chromeCapabilities.setCapability("name", methodName );

				driver = new RemoteWebDriver(new URL(URL), chromeCapabilities);
			} else if ("SauceChromeWithUserAgentPixel".equalsIgnoreCase(browser)) {
				opt.addArguments(
						"--user-agent=Mozilla/5.0 (Linux; Android 8.0.0; Pixel Build/OPR3.170623.008) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.109 Mobile Safari/537.36");
				chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
				chromeCapabilities.setCapability("opera.arguments", "-screenwidth 411 -screenheight 731");
				chromeCapabilities.setPlatform(Platform.fromString(platform));
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				chromeCapabilities.setCapability("name", methodName );
				System.out.println("methodd11111111111111111111"+methodName);
				// driver = new RemoteWebDriver(hubURL, chromeCapabilities);
				driver = new RemoteWebDriver(new URL(URL), chromeCapabilities);

			} else if ("sauceChrome".equalsIgnoreCase(browser)) {
				chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
				chromeCapabilities.setCapability("platform", "Windows 10");
				chromeCapabilities.setCapability("version", "64.0");
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				chromeCapabilities.setCapability("name", methodName );
				System.out.println("methodd11111111111111111111"+methodName);
				driver = new RemoteWebDriver(new URL(URL), chromeCapabilities);

			} else if ("iexplorer".equalsIgnoreCase(browser)) {
				ieCapabilities.setCapability("enablePersistentHover", false);
				ieCapabilities.setCapability("ignoreZoomSetting", true);
				ieCapabilities.setCapability("nativeEvents", false);
				ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
						true);
				ieCapabilities.setPlatform(Platform.fromString(platform));
				driver = new RemoteWebDriver(hubURL, ieCapabilities);
			} else if ("firefox".equalsIgnoreCase(browser)) {
				firefoxCapabilities.setCapability("unexpectedAlertBehaviour", "ignore");
				firefoxCapabilities.setPlatform(Platform.fromString(platform));
				driver = new RemoteWebDriver(hubURL, firefoxCapabilities);
			} else if ("sauceFirefox".equalsIgnoreCase(browser)) {
				firefoxCapabilities.setCapability("platform", "Windows 10");
				firefoxCapabilities.setCapability("version", "58.0");
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				firefoxCapabilities.setCapability("name", methodName );
				System.out.println("methodd11111111111111111111"+methodName);
				driver = new RemoteWebDriver(new URL(URL), firefoxCapabilities);
			} else if ("sauceSafari".equalsIgnoreCase(browser)) {
				safariCapabilities.setCapability("platform", "macOS 10.12");
				safariCapabilities.setCapability("version", "11.0");
				String methodName = new Exception().getStackTrace()[1].getMethodName();
				safariCapabilities.setCapability("name", methodName );
				System.out.println("methodd11111111111111111111"+methodName);
				driver = new RemoteWebDriver(new URL(URL), safariCapabilities);

			} else {
				synchronized (WebDriverFactory.class) {
					firefoxCapabilities.setCapability("unexpectedAlertBehaviour", "ignore");
					firefoxCapabilities.setPlatform(Platform.fromString(platform));
					driver = new RemoteWebDriver(hubURL, firefoxCapabilities);
				}
				driver.manage().timeouts().pageLoadTimeout(maxPageLoadWait, TimeUnit.SECONDS);
			}
			Assert.assertNotNull(driver,
					"Driver did not intialize...\n Please check if hub is running / configuration settings are corect.");

			if ((!"ANDROID".equalsIgnoreCase(platform)) && !("chromewithuseragent".equalsIgnoreCase(browser))
					&& !("SauceChromeWithUserAgentPixel".equalsIgnoreCase(browser))) {
				driver.manage().window().maximize();
			}
		} catch (UnreachableBrowserException e) {
			e.printStackTrace();

			throw new SkipException("Hub is not started or down.");
		} catch (WebDriverException e) {

			try {
				if (driver != null) {
					driver.quit();
				}
			} catch (Exception e1) {
				e.printStackTrace();
			}

			if (e.getMessage().toLowerCase().contains("error forwarding the new session empty pool of vm for setup")) {
				throw new SkipException("Node is not started or down.");
			} else if (e.getMessage().toLowerCase()
					.contains("error forwarding the new session empty pool of vm for setup")
					|| e.getMessage().toLowerCase().contains("cannot get automation extension")
					|| e.getMessage().toLowerCase().contains("chrome not reachable")) {
				Log.message("&emsp;<b> --- Re-tried as browser crashed </b>");
				try {
					driver.quit();
				} catch (WebDriverException e1) {
					e.printStackTrace();
				}
				driver = get(xmlRead);
			} else {
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception encountered in getDriver Method." + e.getMessage().toString());
		} finally {
			// ************************************************************************************************************
			// * Update start time of the tests once free slot is assigned by
			// RemoteWebDriver - Just for reporting purpose
			// *************************************************************************************************************/
			try {
				Field f = Reporter.getCurrentTestResult().getClass().getDeclaredField("m_startMillis");
				f.setAccessible(true);
				f.setLong(Reporter.getCurrentTestResult(), Calendar.getInstance().getTime().getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Log.event("Driver::Get", StopWatch.elapsedTime(startTime));
		/*if (xmlRead)
			Log.addTestRunMachineInfo(driver);*/

		if (System.getProperty("har") == "true") {
			// enable more detailed HAR capture, if desired (see CaptureType for the
			// complete list)
			proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

			// create a new HAR with the label "seleniumeasy.com"
			proxy.newHar("NBC.COM");
		}

		return driver;

	}

	/**
	 * Get the test session Node IP address,port when executing through Grid
	 * 
	 * @param driver
	 *            : Webdriver
	 * @return: Session ID
	 * @throws Exception
	 *             : Selenium Exception
	 */
	public static final String getTestSessionNodeIP(final WebDriver driver) throws Exception {

		XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost")
				: test.getParameter("deviceHost");
		driverPort = test.getParameter("devicePort");
		HttpHost host = new HttpHost(driverHost, Integer.parseInt(driverPort));
		HttpClient client = HttpClientBuilder.create().build();
		URL testSessionApi = new URL("http://" + driverHost + ":" + driverPort + "/grid/api/testsession?session="
				+ ((RemoteWebDriver) driver).getSessionId());
		BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST",
				testSessionApi.toExternalForm());
		HttpResponse response = client.execute(host, r);
		JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
		String nodeIP = object.getString("proxyId").toLowerCase();
		nodeIP = nodeIP.replace("http://", "");
		nodeIP = nodeIP.replaceAll(":[0-9]{1,5}", "").trim();
		return nodeIP;
	}

	/**
	 * Get the test session Hub IP address,port when executing through Grid
	 * 
	 * @param driver
	 *            : Webdriver
	 * @return: Session ID
	 * @throws Exception
	 *             : Selenium Exception
	 */
	public static final String getHubSession(final WebDriver driver) throws Exception {

		XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost")
				: test.getParameter("deviceHost");
		driverPort = test.getParameter("devicePort");
		HttpHost host = new HttpHost(driverHost, Integer.parseInt(driverPort));
		HttpClient client = HttpClientBuilder.create().build();
		URL testSessionApi = new URL("http://" + driverHost + ":" + driverPort + "/grid/api/testsession?session="
				+ ((RemoteWebDriver) driver).getSessionId());
		BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST",
				testSessionApi.toExternalForm());
		HttpResponse response = client.execute(host, r);
		JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
		String nodeIP = object.getString("proxyId").toLowerCase();
		nodeIP = nodeIP.replace("http://", "");
		nodeIP = nodeIP.replaceAll(":[0-9]{1,5}", "").trim();
		return nodeIP;
	}

}

package com.nbc.pages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.nbc.support.Log;
import com.nbc.support.StopWatch;
import com.nbc.support.Utils;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

/**
 * 
 * @author harish.subramani
 * 
 */
public class HomePage extends LoadableComponent<HomePage> implements SpellCheckListener {

	private String appURL;
	private WebDriver driver;
	private boolean isPageLoaded;
	private static String articleWithAdvanceTag = "https://www.nbcnewyork.com/news/local/Ray-Rice-Appeal-Hearing-Janay-Palmer-Roger-Goodell-Atlantic-City-NFL-Domestic-Abuse-281594071.html";

	/**********************************************************************************************
	 ********************************* WebElements of Home Page ***********************************
	 **********************************************************************************************/
	@FindBy(css = "body[id='home']")
	WebElement homePageBody;

	/***********************************
	 * WebElements of Header Link section
	 ***********************************/

	@FindBy(css = "li[class*='Home'] a[lid*='Home']")
	WebElement lnkHome;

	@FindBy(css = "li[class*='News'] a[lid*='News']")
	WebElement lnkNews;

	@FindBy(css = "div[class='brand'] a[data-lid='logo'] img")
	WebElement imgLogo;

	@FindBy(css = "div[class='stack-section'][data-tb-region='Local'] ul li[data-tb-owning-region-name='Local']")
	List<WebElement> localStakeSection;

	@FindBy(css = "div[class='top-stories'] div[class='story'] div[class='headline'] a div[class='headline-tag']")
	List<WebElement> topSectionWithVideos;

	/************************************
	 * WebElements of Header Link section
	 ***********************************/

	/***************************************
	 * WebElements of Weather module
	 ***************************************/

	@FindBy(css = "div[class*='SmartRadar-pane'] img[class*='leaflet-zoom-animated']")
	WebElement radar1;

	@FindBy(css = "div[class*='leaflet-tile-container leaflet-zoom-animated'] img[class*='leaflet-tile leaflet-tile-loaded']")
	WebElement radar;

	@FindBy(css = "div[class='globalRightRail']>div[class='weather-module'], div[class='globalRightRail']>div[class='weather-module-severe']")
	WebElement weatherModule;

	@FindBy(css = "div[class='weather-alert-info'] div[class='alerts']")
	WebElement weatherModuleAlerts;

	@FindBy(css = "div[class='weather-module-location'] div[class='weather-module-city']")
	WebElement currentLocation;

	@FindBy(css = "div[class='weather-module-current']")
	WebElement currentWeather;

	@FindBy(css = "div[class='weather-module-days'] div[class='weather-module-day']")
	List<WebElement> weatherForDays;

	/*****************************************
	 * WebElements of Weather module
	 ***************************************/

	/*****************************************
	 * WebElements of Connect Link
	 *****************************************/

	@FindBy(css = "li[class='nav-small-section nav-connect'] a")
	WebElement lnkConnect;

	@FindBy(css = "ul[class='nav-small'] div[class='subnav-large-container']")
	WebElement lnkConnectAfterHover;

	@FindBy(css = "ul[class='nav-small'] div[class='subnav-large-container'] div[class='nav-connect-sub'] div[class='connect-media']")
	WebElement lnkConnectAfterHoverPanel_Media;

	@FindBy(css = "ul[class='nav-small'] div[class='subnav-large-container'] div[class='nav-connect-sub'] div[class='connect-responds']")
	WebElement lnkConnectAfterHoverPanel_Responds;

	@FindBy(css = "ul[class='nav-small'] div[class='subnav-large-container'] div[class='nav-connect-footer']")
	WebElement lnkConnectAfterHoverPanel_Footer;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-social'] div[class='connect-social-icons'] a[data-lid='Twitter']>i[class='fa fa-twitter']")
	WebElement lnkConnectTwitter;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-social'] div[class='connect-social-icons'] a[data-lid='Instagram']>i[class='fa fa-instagram']")
	WebElement lnkConnectInstagram;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-social'] div[class='connect-social-icons'] a[data-lid='Facebook']>i[class='fa fa fa-facebook-official']")
	WebElement lnkConnectFacebook;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-apps'] a[data-lid='Mobile']")
	WebElement lnkConnectApps;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-email'] a[data-lid='Newsletters']")
	WebElement lnkConnectEmail_NewsLetters;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-ugc'] a[data-lid='UGC'] div[class='connect-ugc-icon'] i[class='fa fa-camera']")
	WebElement lnkConnectSendUsVideosandPictures;

	@FindBy(css = "div[class='nav-connect-sub'] div[class='connect-media'] div[class='connect-send'] a[data-lid='Submit Tips'] div[class='connect-apps-icon'] div[class='connect-tips']")
	WebElement lnkConnectSendTips;

	@FindBy(css = "div[class='nav-connect-footer'] ul li[class='submit-tips'] a[data-lid='Submit Tips']")
	WebElement lnkFooterConnectSendTips;

	@FindBy(css = "div[class='nav-connect-footer'] ul li[class='send-feedback'] a[data-lid='Send Feedback']")
	WebElement lnkFooterConnectSendFeedback;

	@FindBy(css = "div[class='nav-connect-footer'] ul li[class='terms'] a[data-lid='Terms of Service']")
	WebElement lnkFooterConnectTermsofService;

	@FindBy(css = "div[class='nav-connect-footer'] ul li[class='privacy-policy'] a[data-lid='Privacy Policy']")
	WebElement lnkFooterConnectPrivacyPolicy;

	@FindBy(css = "div[class='nav-connect-footer'] ul li[class='partner'] div[class='connect-brand'] a[data-lid='Duopoly']")
	WebElement lnkFooterConnectPartnerLink;

	/*****************************************
	 * WebElements of Connect Link
	 *****************************************/

	/*****************************************
	 * WebElements of Watch Live TV Link
	 *****************************************/

	@FindBy(css = "li[class='nav-small-section nav-live-tv'] a")
	WebElement lnkWatchLiveTV;

	@FindBy(css = "li[class='nav-small-section nav-live-tv'] ul[class='nav-small-sub']")
	WebElement lnkWatchLiveTVAfterHover;

	@FindBy(css = "li[class='nav-small-section nav-live-tv'] ul[class='nav-small-sub'] li[class='livetv']")
	WebElement lnkWatchLiveTVAfterHover_LiveTV;

	@FindBy(css = "li[class='nav-small-section nav-live-tv'] ul[class='nav-small-sub'] li[class='onnow']")
	WebElement lnkWatchLiveTVAfterHover_OnNow;

	@FindBy(css = "li[class='nav-small-section nav-live-tv'] ul[class='nav-small-sub'] li[class='ondemand']")
	WebElement lnkWatchLiveTVAfterHover_OnDemand;

	@FindBy(css = "li[class='nav-small-section nav-live-tv'] ul[class='nav-small-sub'] li[class='schedule'] a[data-lid='Click for full schedule']")
	WebElement lnkWatchLiveTVAfterHover_FullSchedule;

	@FindBy(css = "div[class='daySelectWrapper']")
	WebElement daySelector;

	@FindBy(css = "li[data-type='local']")
	WebElement tabLocalTV;

	@FindBy(css = "li[data-type='cozi']")
	WebElement tabCoziTV;

	/*****************************************
	 * WebElements of Connect Link
	 *****************************************/

	/*****************************************
	 * WebElements of Watch Live TV Link
	 *****************************************/

	@FindBy(css = "div[class='globalRightRail'] div[class*='homepage-module spredfast']")
	WebElement spredfastFeed;

	@FindBy(css = "div[class='globalRightRail'] div[class*='homepage-module spredfast'] h4")
	WebElement spredfastFeedThisJustIn;

	@FindBy(css = "div[class='globalRightRail'] div[class*='homepage-module spredfast'] p[class='reltime'] a[href*='twitter']")
	List<WebElement> liveSpredfastFeed;

	/*****************************************
	 * WebElements of Connect Link
	 *****************************************/

	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Ends ****************************
	 **********************************************************************************************/

	/**
	 * constructor of the class
	 * 
	 * @param driver
	 *            : Webdriver
	 * 
	 * @param url
	 *            : UAT URL
	 */
	public HomePage(WebDriver driver, String url) {
		appURL = url;
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, Utils.maxElementWait);
		PageFactory.initElements(finder, this);
	}// HomePage

	@Override
	protected void isLoaded() {

		Utils.waitForPageLoad(driver);

		if (!isPageLoaded) {
			Assert.fail();
		}

		Utils.waitForPageLoad(driver);

		if (isPageLoaded && !(Utils.waitForElement(driver, lnkNews))) {
			try {
				Log.fail("Home Page did not open up. Site might be down.", driver);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

	}// isLoaded

	@Override
	protected void load() {

		isPageLoaded = true;
		driver.get(appURL);
		Utils.waitForPageLoad(driver);

		try {
			Utils.switchWindows(driver, "Home Page", "title", "false");
		} catch (Exception e) {
		}

		Utils.waitForElement(driver, lnkNews);

	}// load

	/**
	 * Click first article on Local Stack Section
	 */
	public ArticlePage clickOnFirstArticle_OnLocalStackSection() {
		final long startTime = StopWatch.startTime();
		Utils.waitForElement(driver, localStakeSection.get(0));
		localStakeSection.get(0).click();
		Utils.waitForElement(driver, lnkNews);
		Log.event("Clicked first article on Local Stack Section", StopWatch.elapsedTime(startTime));
		return new ArticlePage(driver).get();
	} // clickOnFirstArticle_OnStackSection

	/**
	 * Click first article with Video on Top Section
	 */
	public ArticlePage clickOnFirstArticleWithVideo_OnTopSection() {
		final long startTime = StopWatch.startTime();
		Utils.waitForElement(driver, topSectionWithVideos.get(0));

		for (int i = 0; i < topSectionWithVideos.size(); i++) {
			if("VIDEO".equalsIgnoreCase(topSectionWithVideos.get(i).getText())){
				topSectionWithVideos.get(i).click();
				break;
			}
		}

		Log.event("Clicked first article with Video on Top Section", StopWatch.elapsedTime(startTime));
		return new ArticlePage(driver).get();
	} // clickOnFirstArticleWithVideo_OnTopSection

	/**
	 * Navigate to Article with Advacne Tagging Page
	 */
	public ArticlePage navigateToArticleWithAdvacneTagging() {
		final long startTime = StopWatch.startTime();
		driver.get(articleWithAdvanceTag);
		Log.event("Navigated to Article with Advacne Tagging Page", StopWatch.elapsedTime(startTime));
		return new ArticlePage(driver).get();
	} // navigateToArticleWithAdvacneTagging

	/**
	 * Navigate to News Page
	 * @throws Exception 
	 */
	public void navigateToNewsPage() throws Exception {
		final long startTime = StopWatch.startTime();
		Utils.waitForElement(driver, imgLogo);
		lnkNews.click();
		Utils.waitForElement(driver, lnkNews);
		Log.assertThatForEvent((lnkNews.getAttribute("class").contains("nav-selected")), "Clicked on News link..",
				"Error occurred while clicking on News Link", startTime, driver);
	} // navigateToNewsPage

	/**
	 * Navigate to Home Page
	 * @throws Exception 
	 */
	public void clickOnLogo() throws Exception {
		final long startTime = StopWatch.startTime();
		Utils.waitForElement(driver, imgLogo);
		imgLogo.click();
		Log.assertThatForEvent((lnkHome.getAttribute("class").contains("nav-selected")), "Clicked on Home link..",
				"Error occurred while clicking on Home Link", startTime, driver);
	} // navigateToNewsPage

	/**
	 * Hover over Connect Link
	 * @throws Exception 
	 */
	public void doMouseHoverOnConnectLink() throws Exception {
		final long startTime = StopWatch.startTime();
		Utils.waitForElement(driver, lnkConnect);

		Actions action = new Actions(driver);
		action.moveToElement(lnkConnect).build().perform();

		Log.assertThatForEvent((lnkConnectAfterHover.getAttribute("style").contains("block")), "Hovered Connect Link..",
				"Error occurred while hovering on Connect Link", startTime, driver);
	} // doMouseHoverOnConnectLink

	/**
	 * Hover over Watch Live TV Link
	 * @throws Exception 
	 */
	public void doMouseHoverOnWatchLiveTVLink() throws Exception {
		final long startTime = StopWatch.startTime();
		Utils.waitForElement(driver, lnkWatchLiveTV);

		Actions action = new Actions(driver);
		action.moveToElement(lnkWatchLiveTV).build().perform();

		Log.assertThatForEvent((lnkWatchLiveTVAfterHover.getAttribute("style").contains("block")),
				"Hovered Watch Live TV Link..", "Error occurred while hovering on Watch Live TV Link", startTime,
				driver);
	} // doMouseHoverOnWatchLiveTVLink

	/**
	 * Navigate to Full Schedule On Live TV
	 * @throws Exception 
	 */
	public void navigateToFullScheduleOnLiveTV() throws Exception {
		final long startTime = StopWatch.startTime();

		Utils.waitForElement(driver, lnkWatchLiveTV);
		Actions action = new Actions(driver);
		action.moveToElement(lnkWatchLiveTV).build().perform();

		Utils.waitForElement(driver, lnkWatchLiveTVAfterHover_FullSchedule);
		lnkWatchLiveTVAfterHover_FullSchedule.click();

		Log.assertThatForEvent((daySelector.isDisplayed()), "Navigated to Full Schedule On Live TV..",
				"Error occurred while navigate to Full Schedule On Live TV", startTime, driver);
	} // navigateToFullScheduleOnLiveTV

	/**
	 * Method to validate presence of 'Logo' in Home Page
	 */
	public boolean validateLogo() {
		long startTime = StopWatch.startTime();
		boolean status = false;

		try {
			if (imgLogo.isDisplayed() & imgLogo.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Brand Logo...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateLogo

	/**
	 * Method to validate Home Page
	 */
	public boolean validateHomePage() {
		long startTime = StopWatch.startTime();
		boolean status = false;

		try {
			if (homePageBody.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Home Page...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateHomePage

	/**
	 * Method to validate Radar on Weather Module
	 */
	public boolean validateRadarOnWeatherModule() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, 
				driver.findElement((By.cssSelector("iframe[class='wx-standalone-map']"))));

		try {
			driver.switchTo().frame(
					driver.findElement((By.cssSelector("iframe[class='wx-standalone-map']"))));
			Utils.waitForElement(driver, radar1);
			if (radar.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Radar...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateRadarOnWeatherModule

	/**
	 * Method to validate Weather For future Days
	 */
	public boolean validateWeatherForDays() {
		long startTime = StopWatch.startTime();
		boolean status = false;

		Utils.waitForElement(driver, weatherForDays.get(0));
		if (weatherForDays.size() == 3)
			status = true;
		else
			status = false;

		Log.event("Validating weatherForDays...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateRadarOnWeatherModule

	/**
	 * Method to validate Weather Module
	 */
	public boolean validateWeatherModule() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, weatherModule);

		try {
			if (weatherModule.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Weather Module...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateWeatherModule

	/**
	 * Method to validate Weather Alert Module
	 */
	public boolean validateWeatherAlertModule() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, weatherModuleAlerts);

		try {
			if (weatherModuleAlerts.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Weather Module...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateWeatherAlertModule

	/**
	 * Method to validate Connect Hover Panel
	 */
	public boolean validateConnectHoverPanel() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectAfterHoverPanel_Media);

		try {
			if ((lnkConnectAfterHoverPanel_Media.isDisplayed() && lnkConnectAfterHoverPanel_Media.isEnabled())
					&& (lnkConnectAfterHoverPanel_Responds.isDisplayed()
							&& lnkConnectAfterHoverPanel_Responds.isEnabled())
					&& (lnkConnectAfterHoverPanel_Footer.isDisplayed() && lnkConnectAfterHoverPanel_Footer.isEnabled()))
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Hover Panel...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectHoverPanel

	/**
	 * Method to validate Watch Live TV Hover Panel
	 */
	public boolean validateWatchLiveTVHoverPanel() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkWatchLiveTVAfterHover_LiveTV);

		try {
			if ((lnkWatchLiveTVAfterHover_LiveTV.isDisplayed() && lnkWatchLiveTVAfterHover_LiveTV.isEnabled())
					&& (lnkWatchLiveTVAfterHover_OnNow.isDisplayed() && lnkWatchLiveTVAfterHover_OnNow.isEnabled())
					&& (lnkWatchLiveTVAfterHover_OnDemand.isDisplayed()
							&& lnkWatchLiveTVAfterHover_OnDemand.isEnabled())
					&& (lnkWatchLiveTVAfterHover_FullSchedule.isDisplayed()
							&& lnkWatchLiveTVAfterHover_FullSchedule.isEnabled()))
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Hover Panel...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateWatchLiveTVHoverPanel

	/**
	 * Method to validate Spredfast Module
	 */
	public boolean validateSpredfastModule() {
		long startTime = StopWatch.startTime();
		boolean status = false;

		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", spredfastFeed);

			if (spredfastFeed.isDisplayed() && spredfastFeed.isEnabled()
					&& ((spredfastFeedThisJustIn.getText().toUpperCase().contains("THIS JUST IN"))))
				status = true;

		} catch (NoSuchElementException e) {
			status = false;
		}
		driver.switchTo().defaultContent();
		Log.event("Validating Spredfast Module...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateSpredfastModule

	/**
	 * Method to validate Spredfast Feed
	 */
	public boolean validateSpredfastFeed() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		boolean preStatus = true;
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", spredfastFeed);

			for (int i = 0; i < liveSpredfastFeed.size(); i++) {
				if ((liveSpredfastFeed.get(0).getText().contains("hours ago"))
						|| (liveSpredfastFeed.get(0).getText().contains("hour ago"))
						|| (liveSpredfastFeed.get(0).getText().contains("minutes ago"))
						|| (liveSpredfastFeed.get(0).getText().contains("minute ago"))
						|| (liveSpredfastFeed.get(0).getText().contains("seconds ago"))
						|| (liveSpredfastFeed.get(0).getText().contains("second ago")))
					preStatus = true;
				else
					preStatus = false;
			}

			if (spredfastFeed.isDisplayed() && spredfastFeed.isEnabled()
					&& ((liveSpredfastFeed.size() > 3) /*&& (liveSpredfastFeed.size() == 7)*/) && preStatus)
				status = true;

		} catch (NoSuchElementException e) {
			status = false;
		}
		driver.switchTo().defaultContent();
		Log.event("Validating Spredfast Feed...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateSpredfastFeed

	/**
	 * Method to validate TV Listing
	 */
	public boolean validateTVListing() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, daySelector);

		try {
			if ((driver.getCurrentUrl().contains("tv-listings")) && (tabLocalTV.isDisplayed() && tabLocalTV.isEnabled())
					&& (tabCoziTV.isDisplayed() && tabCoziTV.isEnabled())
					&& (daySelector.isDisplayed() && daySelector.isEnabled())
					&& (driver.findElement(By.cssSelector(
							"div[id='listing0'] div[aria-live='polite'] div[data-slick-index='0']>div[class='time']"))
							.getText().contains("NOW")))
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating TV Listing...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateTVListing

	/**
	 * Method to validate Connect Twitter
	 */
	public boolean validateConnectTwitter() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectTwitter);

		try {
			if (lnkConnectTwitter.isDisplayed() && lnkConnectTwitter.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Twitter...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectTwitter

	/**
	 * Method to validate Connect Instagram
	 */
	public boolean validateConnectInstagram() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectInstagram);

		try {
			if (lnkConnectInstagram.isDisplayed() && lnkConnectInstagram.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Instagram...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectInstagram

	/**
	 * Method to validate Connect Facebook
	 */
	public boolean validateConnectFacebook() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectFacebook);

		try {
			if (lnkConnectFacebook.isDisplayed() && lnkConnectFacebook.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Facebook...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectFacebook

	/**
	 * Method to validate Connect Apps
	 */
	public boolean validateConnectApps() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectApps);

		try {
			if (lnkConnectApps.isDisplayed() && lnkConnectApps.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Apps...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectApps

	/**
	 * Method to validate Connect Email/NewsLetters
	 */
	public boolean validateConnectEmailNewsLetters() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectEmail_NewsLetters);

		try {
			if (lnkConnectEmail_NewsLetters.isDisplayed() && lnkConnectEmail_NewsLetters.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Email/NewsLetters...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectEmailNewsLetters

	/**
	 * Method to validate Connect SendUsVideosandPictures
	 */
	public boolean validateConnectSendUsVideosandPictures() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectSendUsVideosandPictures);

		try {
			if (lnkConnectSendUsVideosandPictures.isDisplayed() && lnkConnectSendUsVideosandPictures.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Email/NewsLetters...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectSendUsVideosandPictures

	/**
	 * Method to validate Connect Send Tips
	 */
	public boolean validateConnectSendTips() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkConnectSendTips);

		try {
			if (lnkConnectSendTips.isDisplayed() && lnkConnectSendTips.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Connect Send Tips...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateConnectSendTips

	/**
	 * Method to validate Footer Connect Send Tips
	 */
	public boolean validateFooterConnectSendTips() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkFooterConnectSendTips);

		try {
			if (lnkFooterConnectSendTips.isDisplayed() && lnkFooterConnectSendTips.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Footer Connect Send Tips...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateFooterConnectSendTips

	/**
	 * Method to validate Footer Connect Send Feedback
	 */
	public boolean validateFooterConnectSendFeedback() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkFooterConnectSendFeedback);

		try {
			if (lnkFooterConnectSendFeedback.isDisplayed() && lnkFooterConnectSendFeedback.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Footer Connect Send Feedback...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateFooterConnectSendFeedback

	/**
	 * Method to validate Footer Connect Terms of Service
	 */
	public boolean validateFooterConnectTermsofService() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkFooterConnectTermsofService);

		try {
			if (lnkFooterConnectTermsofService.isDisplayed() && lnkFooterConnectTermsofService.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Footer Connect Terms of Service...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateFooterConnectTermsofService

	/**
	 * Method to validate Footer Connect Privacy Policy
	 */
	public boolean validateFooterConnectPrivacyPolicy() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkFooterConnectPrivacyPolicy);

		try {
			if (lnkFooterConnectPrivacyPolicy.isDisplayed() && lnkFooterConnectPrivacyPolicy.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Footer Connect Privacy Policy...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateFooterConnectPrivacyPolicy

	/**
	 * Method to validate Footer Connect Partner
	 */
	public boolean validateFooterConnectPartner() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkFooterConnectPartnerLink);

		try {
			if (lnkFooterConnectPartnerLink.isDisplayed() && lnkFooterConnectPartnerLink.isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Footer Connect Partner...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateFooterConnectPartner

	/**
	 * Method to validate Page Spelling
	 */
	public String checkPageSpelling(boolean appendToFile) {
		String dictFile = "dict/english.0";
		String phonetFile = "dict/phonet.en";
		long startTime = StopWatch.startTime();
		String source = driver.findElement(By.tagName("body")).getText();
		SpellChecker spellCheck = null;
		PrintWriter out = null;
		int errors = -1, error = 0;
		try {
			SpellDictionary dictionary = new SpellDictionaryHashMap(new File(dictFile), new File(phonetFile));

			spellCheck = new SpellChecker(dictionary);
			spellCheck.addSpellCheckListener((SpellCheckListener) this);

			if (source.length() != 0) {
				File spellCheckList = new File("C:\\SpellCheck\\SpellCheck.txt");
				out = new PrintWriter(new FileOutputStream(spellCheckList,appendToFile));
				out.println(new SimpleDateFormat("dd MMM HH:mm:ss SSS").format(Calendar.getInstance().getTime())+"==================================="+driver.getCurrentUrl()+"===================================");
				for(String stringToBeValidated : source.split("\\n"))
				{
					if(stringToBeValidated!=null && !stringToBeValidated.isEmpty() && stringToBeValidated!="") {
					
						error = spellCheck.checkSpelling(new StringWordTokenizer(stringToBeValidated));
						if(error>0) {
							out.append("INCORRECT - "+stringToBeValidated+System.lineSeparator());
							errors++;
						} else {
							out.append("CORRECT - "+stringToBeValidated+System.lineSeparator());
						}
					}
				}
				
				if(errors == -1) {
					return spellCheckList.getPath()+"_true";
				} else if (errors >= 0) {
					return spellCheckList.getPath()+"_false_"+(++errors);
				}
			}
				
			Log.event("Checking Spelling on Page...", StopWatch.elapsedTime(startTime));	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
		
		return "false";
	}// checkPageSpelling

	@SuppressWarnings("rawtypes")
	public void spellingError(SpellCheckEvent event) {
		List suggestions = event.getSuggestions();
		if (suggestions != null) {
			if (suggestions.size() > 0) {
				Log.message("MISSPELT WORD: " + event.getInvalidWord());
				for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();) {
					Log.message("tSuggested Word: " + suggestedWord.next());
				}
			} else {
				Log.message("MISSPELT WORD: " + event.getInvalidWord());
				Log.message("No suggestions");
			}
		}
	}

	/**
	 * Method to validate Broken Links
	 * @throws IOException 
	 */
	public String checkBrokenLinks() throws IOException {
		String returnString = "";
		String HOST = driver.getCurrentUrl();
		long startTime = StopWatch.startTime();
		HashSet<String> readSitemap = readSitemap(HOST + "/sitemap.xml");
		for (String href : readSitemap) {
			HashSet<String> linksSet = findHreflinks(new URL(href), HOST);
			for (String link : linksSet) {
				URL url = new URL(link);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				if (connection.getResponseCode() != 200) {
					returnString = url + ": " + connection.getResponseCode();
					System.out.println(url + ": " + connection.getResponseCode());
				}
			}
		}
		Log.event("Checking Broken Links on Page...", StopWatch.elapsedTime(startTime));
		return returnString;
	}// checkBrokenLinks

	private static HashSet<String> readSitemap(String sitemapURL) {
		HashSet<String> set = new HashSet<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			InputSource inputStream = new InputSource(sitemapURL);
			Document document = documentBuilder.parse(inputStream);
			NodeList studentNodeList = document.getElementsByTagName("url");
			for (int index = 0; index < studentNodeList.getLength(); index++) {
				Node node = studentNodeList.item(index);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					NodeList nameNodeList = element.getElementsByTagName("loc");
					for (int eIndex = 0; eIndex < nameNodeList.getLength(); eIndex++) {
						if (nameNodeList.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
							Element nameElement = (Element) nameNodeList.item(eIndex);
							set.add(nameElement.getFirstChild().getNodeValue().trim());
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return set;
	}

	private static HashSet<String> findHreflinks(URL url, String HOST) {
		HashSet<String> set = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str).append("\n");
			}
			in.close();
			Pattern p = Pattern.compile("href=\"(.*?)\"");
			Matcher m = p.matcher(sb);

			String changedURL = "";
			while (m.find()) {
				if (m.group(1).startsWith("//")) {
					changedURL = m.group(1).replace("//", "");
					set.add(changedURL);
				} else if (m.group(1).startsWith("/")) {
					changedURL = m.group(1).replaceFirst("/", HOST);
					set.add(changedURL);
				}
			}
			p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
			m = p.matcher(sb);
			while (m.find()) {
				if (m.group(1).startsWith("/")) {
					changedURL = m.group(1).replaceFirst("/", HOST);
					set.add(changedURL);
				} else {
					changedURL = m.group(1);
					set.add(changedURL);
				}
			}
		} catch (Exception e) {
			System.out.println("Link " + url + " failed");
		}
		return set;
	}
}// HomePage

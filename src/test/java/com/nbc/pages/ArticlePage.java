package com.nbc.pages;

import java.util.List;
import java.util.NoSuchElementException;

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

import com.nbc.support.*;

/**
 * 
 * @author harish.subramani
 * 
 */
public class ArticlePage extends LoadableComponent<ArticlePage> {

	private WebDriver driver;
	private boolean isPageLoaded;

	/**********************************************************************************************
	 ********************************* WebElements of Article Page ********************************
	 **********************************************************************************************/
	@FindBy(css = "body[id='home']")
	WebElement homePageBody;

	@FindBy(css = "div[class='player'] div[class='tpVideoBlocker']")
	WebElement articleVideoPlayer;

	@FindBy(css = "i[class='fa fa-facebook fa-stack-1x fa-inverse']")
	WebElement sharelnkFacebook;

	@FindBy(css = "i[class='fa fa-twitter fa-stack-1x fa-inverse']")
	WebElement sharelnkTwitter;

	@FindBy(css = "i[class='fa fa-comments-o fa-stack-1x fa-inverse']")
	WebElement lnkComments;

	@FindBy(css = "i[class='fa fa-envelope-o fa-stack-1x fa-inverse']")
	WebElement lnkEmail;

	@FindBy(css = "i[class='fa fa-print fa-stack-1x fa-inverse']")
	WebElement lnkPrint;

	@FindBy(css = "div[id='timeline-embed']")
	WebElement htmlModule;

	@FindBy(css="div[class='PlayerLabelFont']")
	List<WebElement> playerTime;

	/**********************************************************************************************
	 ********************************* WebElements of HoArticleme Page - Ends *********************
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
	public ArticlePage(WebDriver driver) {
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

		if (isPageLoaded && !(Utils.waitForElement(driver, sharelnkFacebook))) {			
			try {
				Log.fail("Article Page did not open up. Site might be down.", driver);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

	}// isLoaded

	@Override
	protected void load() {
		isPageLoaded = true;
		Utils.waitForElement(driver, sharelnkFacebook);
	}// load

	/**
	 * Method to validate Article Video Player
	 */
	public boolean validateArticleVideoPlayer() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, sharelnkFacebook);

		try {
			if (articleVideoPlayer.isDisplayed() && articleVideoPlayer.isEnabled()) {
				status = true;
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
						articleVideoPlayer);
			}
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Article Video Player...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateArticleVideoPlayer

	/**
	 * Method to validate Video pre-roll
	 */
	public boolean validatePrerollVideo() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, sharelnkFacebook);
		Utils.waitForElement(driver, playerTime.get(0));

		try {
			if(!playerTime.isEmpty() && playerTime.get(0).getText()!=null && !playerTime.get(0).getText().isEmpty()){

				Log.event("Validating Pre-roll Video Player...totalVideoTime.text"+playerTime.get(2).getText(), StopWatch.elapsedTime(startTime));
				String prerollMinute = playerTime.get(2).getText().split(":")[0];
				int prerollSeconds = Integer.parseInt(playerTime.get(2).getText().split(":")[1]);	

				Actions action = new Actions(driver);
				action.moveToElement(playerTime.get(0)).build().perform();

				Log.event("Validating Pre-roll Video Player...prerollMinute"+prerollMinute, StopWatch.elapsedTime(startTime));
				Log.event("Validating Pre-roll Video Player...prerollSeconds"+prerollSeconds, StopWatch.elapsedTime(startTime));

				if("0".equals(prerollMinute) && playerTime.get(0).isDisplayed() && prerollSeconds<45) {

					action.moveToElement(playerTime.get(0)).build().perform();
					String videoStartTime = playerTime.get(0).getText();
					Log.event("Validating Pre-roll Video Player...videoStartTime"+videoStartTime, StopWatch.elapsedTime(startTime));
					Thread.sleep(17000);
					action.moveToElement(playerTime.get(0)).build().perform();
					Log.event("Validating Pre-roll Video Player...playerTime"+playerTime.get(0).getText(), StopWatch.elapsedTime(startTime));
					if(!playerTime.get(0).getText().equals(videoStartTime))
						status=true;
					else
						status=false;
				}
			}
		} catch (NoSuchElementException e) {
			status = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.event("Validating Pre-roll Video Player...", StopWatch.elapsedTime(startTime));
		return status;
	}// validatePrerollVideo

	/**
	 * Method to validate Facebook Icon
	 */
	public boolean validateFacebookIcon() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, sharelnkFacebook);

		try {
			if (sharelnkFacebook.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Facebook Icon...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateFacebookIcon

	/**
	 * Method to validate Twitter Icon
	 */
	public boolean validateTwitterIcon() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, sharelnkTwitter);

		try {
			if (sharelnkTwitter.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Twitter Icon...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateTwitterIcon

	/**
	 * Method to validate Comments Icon
	 */
	public boolean validateCommentsIcon() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkComments);

		try {
			if (lnkComments.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Comments Icon...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateCommentsIcon

	/**
	 * Method to validate Email Icon
	 */
	public boolean validateEmailIcon() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkEmail);

		try {
			if (lnkEmail.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Email Icon...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateEmailIcon

	/**
	 * Method to validate Twitter Icon
	 */
	public boolean validatePrintIcon() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		Utils.waitForElement(driver, lnkPrint);

		try {
			if (lnkPrint.isDisplayed())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		Log.event("Validating Print Icon...", StopWatch.elapsedTime(startTime));
		return status;
	}// validatePrintIcon

	/**
	 * Method to validate HTML Module
	 */
	public boolean validateHTMLModule() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		String htmlModule = "div[id='timeline-embed']";

		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					driver.findElement((By.cssSelector("p iframe"))));
			driver.switchTo().frame(driver.findElement((By.cssSelector("p iframe"))));
			if (driver.findElement((By.cssSelector(htmlModule))).isDisplayed()
					&& driver.findElement((By.cssSelector(htmlModule))).isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		driver.switchTo().defaultContent();
		Log.event("Validating HTML Module...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateHTMLModule

	/**
	 * Method to validate HTML Module Toolbar
	 */
	public boolean validateHTMLModule_Toolbar() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		String htmlModuleToolBar_BackHome = "div[id='timeline-embed'] div[class='vco-toolbar'] div[class='back-home']";
		String htmlModuleToolBar_ZoomIn = "div[id='timeline-embed'] div[class='vco-toolbar'] div[class='zoom-in']";
		String htmlModuleToolBar_ZoomOut = "div[id='timeline-embed'] div[class='vco-toolbar'] div[class='zoom-out']";

		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					driver.findElement((By.cssSelector("p iframe"))));
			driver.switchTo().frame(driver.findElement((By.cssSelector("p iframe"))));

			if ((driver.findElement((By.cssSelector(htmlModuleToolBar_BackHome))).isDisplayed()
					&& driver.findElement((By.cssSelector(htmlModuleToolBar_BackHome))).isEnabled())
					&& (driver.findElement((By.cssSelector(htmlModuleToolBar_ZoomIn))).isDisplayed()
							&& driver.findElement((By.cssSelector(htmlModuleToolBar_ZoomIn))).isEnabled())
					&& (driver.findElement((By.cssSelector(htmlModuleToolBar_ZoomOut))).isDisplayed()
							&& driver.findElement((By.cssSelector(htmlModuleToolBar_ZoomOut))).isEnabled()))
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		driver.switchTo().defaultContent();
		Log.event("Validating HTML Module - ToolBar...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateHTMLModule_Toolbar

	/**
	 * Method to validate HTML Module Time Navigation
	 */
	public boolean validateHTMLModule_TimeNavigation() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		String htmlModuleNav_TimeNavIndicator = "div[id='timeline-embed'] div[class='vco-navigation'] div[class='timenav-background'] div[class='timenav-indicator']";
		String htmlModuleNav_TimeNavInterval = "div[id='timeline-embed'] div[class='vco-navigation'] div[class='timenav-background'] div[class='timenav-interval-background']";

		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					driver.findElement((By.cssSelector("p iframe"))));
			driver.switchTo().frame(driver.findElement((By.cssSelector("p iframe"))));
			if ((driver.findElement((By.cssSelector(htmlModuleNav_TimeNavIndicator))).isDisplayed()
					&& driver.findElement((By.cssSelector(htmlModuleNav_TimeNavIndicator))).isEnabled())
					&& (driver.findElement((By.cssSelector(htmlModuleNav_TimeNavInterval))).isDisplayed()
							&& driver.findElement((By.cssSelector(htmlModuleNav_TimeNavInterval))).isEnabled()))
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		driver.switchTo().defaultContent();
		Log.event("Validating HTML Module - Time Navigation...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateHTMLModule_TimeNavigation

	/**
	 * Method to validate HTML Module SlideContainer
	 */
	public boolean validateHTMLModuleSlideContainer() {
		long startTime = StopWatch.startTime();
		boolean status = false;
		String htmlModuleSlideContainer = "div[id='timeline-embed'] div[class='vco-slider'] div[class='slider-container-mask'] div[class='slider-container']";

		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
					driver.findElement((By.cssSelector("p iframe"))));
			driver.switchTo().frame(driver.findElement((By.cssSelector("p iframe"))));
			if (driver.findElement((By.cssSelector(htmlModuleSlideContainer))).isDisplayed()
					&& driver.findElement((By.cssSelector(htmlModuleSlideContainer))).isEnabled())
				status = true;
		} catch (NoSuchElementException e) {
			status = false;
		}
		driver.switchTo().defaultContent();
		Log.event("Validating HTML Module...", StopWatch.elapsedTime(startTime));
		return status;
	}// validateHTMLModuleSlideContainer

}// ArticlePage

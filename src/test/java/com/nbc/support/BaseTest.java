package com.nbc.support;

import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.aventstack.extentreports.ExtentReports;

public class BaseTest {
	protected static ExtentReports extent;
	
	@BeforeMethod(alwaysRun = true)
	public void beforeMethod() {

	}
	
	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestContext ctx) {

	}
	
	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() throws Exception {
		//extent = new ExtentReports("./test-output/PoC_Demo.html", true, DisplayOrder.NEWEST_FIRST, NetworkMode.ONLINE);
	}

	
	 /** After suite will be responsible to close the report properly at the end You an have another afterSuite as well in the derived class and this one will be called in the end making it the last
	 * method to be called in test exe*/
	 
	@AfterSuite
	public void afterSuite() {
		//extent.flush();
	}
}

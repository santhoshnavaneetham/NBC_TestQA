package com.nbc.support;

import java.util.List;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

public class DataProviderUtils {

	@DataProvider(parallel = true)
	public static Iterator <Object[]> multiBrowserWebsites(ITestContext context) throws IOException {

		List <Object[]> dataToBeReturned = new ArrayList <Object[]>();
		List <String> browsers = Arrays.asList(context.getCurrentXmlTest().getParameter("browserName").split(","));
		List <String> websites = Arrays.asList(context.getCurrentXmlTest().getParameter("webSite").split(","));

		for (String website : websites) {
			for (String browser : browsers) {
				dataToBeReturned.add(new Object[] { browser, website });
			}
		}

		return dataToBeReturned.iterator();
	}

	@DataProvider(parallel = true)
	public static Iterator <Object[]> multiDataIterator(ITestContext context) throws IOException {

		File dir1 = new File(".");
		String strBasePath = null;
		String browserInputFile = null;
		String paymentTypeInputFile = null;
		strBasePath = dir1.getCanonicalPath();

		List <String> websites = Arrays.asList(context.getCurrentXmlTest().getParameter("webSite").split(","));
		browserInputFile = strBasePath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "DataProviders" + File.separator + context.getCurrentXmlTest().getParameter("browserDataProvider");
		paymentTypeInputFile = strBasePath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "DataProviders" + File.separator + context.getCurrentXmlTest().getParameter("paymentDataProvider");

		// Get a list of String file content (line items) from the test file.
		List <String> browserList = getFileContentList(browserInputFile);
		List <String> paymentTypes = getFileContentList(paymentTypeInputFile);

		// We will be returning an iterator of Object arrays so create that first.
		List <Object[]> dataToBeReturned = new ArrayList <Object[]>();

		// Populate our List of Object arrays with the file content.
		for (String website : websites) {
			for (String browser : browserList) {
				for (String payment : paymentTypes)
					dataToBeReturned.add(new Object[] { browser, website, payment });
			}
		}

		// return the iterator - testng will initialize the test class and calls the
		// test method with each of the content of this iterator.
		return dataToBeReturned.iterator();

	}// multiDataIterator

	/**
	 * Utility method to get the file content in UTF8
	 * 
	 * @param filenamePath
	 * @return
	 */
	private static List <String> getFileContentList(String filenamePath) {
		List <String> lines = new ArrayList <String>();
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filenamePath), "UTF8"));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			br.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

}
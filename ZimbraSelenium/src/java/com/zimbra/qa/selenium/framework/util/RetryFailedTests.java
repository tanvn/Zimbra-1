package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.testng.ITestResult;
import org.testng.util.RetryAnalyzerCount;

import com.zimbra.qa.selenium.framework.core.SelNGBase;


public class RetryFailedTests extends RetryAnalyzerCount {
	public RetryFailedTests() {
		setCount(SelNGBase.maxRetryCount);
	}
	@Override
	public boolean retryMethod(ITestResult result) {
		logRetriedTests(result.getMethod().toString());
		SelNGBase.isExecutionARetry.set(true);
		SelNGBase.needReset.set(false);
		return true;
	}

	private void logRetriedTests(String testName) {
		File retriedTestFile = 
			new File(
				ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot")
				+ "/" + ZimbraSeleniumProperties.getAppType().toString() + "/retriedTests.txt");
	    if(!retriedTestFile.exists())
			try {
				retriedTestFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}		

		try {
			Writer output = new BufferedWriter(new FileWriter(retriedTestFile,
					true));
			output.write(testName + "\n");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package com.sanjoyghosh.company.gwt;

import com.sanjoyghosh.company.gwt.client.CompanyGWTTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CompanyGWTSuite extends GWTTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for CompanyGWT");
		suite.addTestSuite(CompanyGWTTest.class);
		return suite;
	}
}

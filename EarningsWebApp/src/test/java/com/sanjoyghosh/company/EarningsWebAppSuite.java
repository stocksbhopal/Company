package com.sanjoyghosh.company;

import com.sanjoyghosh.company.client.EarningsWebAppTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class EarningsWebAppSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for EarningsWebApp");
    suite.addTestSuite(EarningsWebAppTest.class);
    return suite;
  }
}

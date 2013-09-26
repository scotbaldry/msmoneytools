package com.scotbaldry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FidelityFundPricesCSVParserTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FidelityFundPricesCSVParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(FidelityFundPricesCSVParserTest.class);
    }

    public void testParseValidFile() {
        FidelityFundPricesCSVParser fidelityCSVParser = new FidelityFundPricesCSVParser("/Users/scot/Downloads/data.csv");
        fidelityCSVParser.parse();
        assertEquals("Check number of rows is correct", 96, fidelityCSVParser.getRowCount());
    }

    public void testParseInvalidFile() {
        //TODO: complete this
    }
}

package com.scotbaldry.msmoneytools;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FidelityHoldingsCSVParserTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FidelityHoldingsCSVParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(FidelityHoldingsCSVParserTest.class);
    }

    public void testParseValidFile() throws Exception {
        FidelityHoldingsCSVParser fidelityCSVParser = new FidelityHoldingsCSVParser("/Users/scot/Downloads/AllHoldings.csv", null); //todo
        fidelityCSVParser.parse();
        assertEquals("Check number of rows is correct", 26, fidelityCSVParser.getRowCount());
    }

    public void testParseInvalidFile() {
        FidelityHoldingsCSVParser fidelityCSVParser = new FidelityHoldingsCSVParser("/Users/scot/Downloads/data.csv", null); //todo
        try {
            fidelityCSVParser.parse();
        } catch (Exception e) {
            assertTrue("Check that Exception thrown if trying to parse incorrect file format", true);
        }
    }
}

package com.scotbaldry.msmoneytools;

import java.io.File;
import java.net.URL;

import com.scotbaldry.msmoneytools.parsers.FidelityFundPricesCSVParser;
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
        URL fundPricesFilename = ClassLoader.getSystemResource("fidelity_fund_prices.csv");
        FidelityFundPricesCSVParser fidelityCSVParser = new FidelityFundPricesCSVParser();
        fidelityCSVParser.parse(new File(fundPricesFilename.getFile()));
        assertEquals("Check number of rows is correct", 230, fidelityCSVParser.getRowCount());
    }

    public void testParseInvalidFile() {
        //TODO: add test here for invalid file
    }
}

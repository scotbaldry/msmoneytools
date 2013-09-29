package com.scotbaldry.msmoneytools;


import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang.time.DateUtils;

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
        URL holdingsFilename = ClassLoader.getSystemResource("fidelity_holdings.csv");
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser(holdingsFilename.getFile(), null); //todo
        parser.parse();
        assertEquals("Check number of rows is correct", 2, parser.getRowCount());

        Date refDate = new GregorianCalendar(2013, Calendar.SEPTEMBER, 26).getTime();
        assertTrue("Check valuation date is correct", DateUtils.isSameDay(refDate, parser.getValuationDate()));
    }

    public void testParseInvalidFile() {
        URL holdingsFilename = ClassLoader.getSystemResource("fidelity_holdings.csv");
        FidelityHoldingsCSVParser fidelityCSVParser = new FidelityHoldingsCSVParser(holdingsFilename.getFile(), null); //todo
        try {
            fidelityCSVParser.parse();
        } catch (Exception e) {
            assertTrue("Check that Exception thrown if trying to parse incorrect file format", true);
        }
    }
}

package com.scotbaldry.msmoneytools;


import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.scotbaldry.msmoneytools.parsers.FidelityHoldingsCSVParser;
import com.scotbaldry.msmoneytools.parsers.MapperParser;
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
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser(null); //todo
        parser.parse(new File(holdingsFilename.getFile()));
        assertEquals("Check number of rows is correct", 2, parser.getRowCount());

        Date refDate = new GregorianCalendar(2013, Calendar.SEPTEMBER, 26).getTime();
        assertTrue("Check valuation date is correct", DateUtils.isSameDay(refDate, parser.getValuationDate()));
    }

    public void testParseInvalidFile() {
        URL holdingsFilename = ClassLoader.getSystemResource("fidelity_holdings.csv");
        FidelityHoldingsCSVParser fidelityCSVParser = new FidelityHoldingsCSVParser(null); //todo
        try {
            fidelityCSVParser.parse(new File(holdingsFilename.getFile()));
        } catch (Exception e) {
            assertTrue("Check that Exception thrown if trying to parse incorrect file format", true);
        }
    }

    public void testGetData() throws Exception {
        int checkedItems = 0;

        URL holdingsFilename = ClassLoader.getSystemResource("fidelity_holdings.csv");
        URL mappingFilename = ClassLoader.getSystemResource("fidelity_mappings_1.csv");
        MapperParser mapperParser = new MapperParser();
        mapperParser.parse(new File(mappingFilename.getFile()));
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser(mapperParser);
        parser.parse(new File(holdingsFilename.getFile()));
        Object[][] data = parser.getData();
        assertEquals("Check size of returned data array", 2, data.length);

        if (data[0][0].equals("GB00B7FQHJ97")) {
            assertEquals("Check symbol", "GB00B7FQHJ97", data[0][0]);
            assertEquals("Check security name", "Fidelity Global Dividend Fund", data[0][1]);
            assertEquals("Check price", "130.4", data[0][2]);
            assertEquals("Check currency", "GBP", data[0][3]);
            checkedItems++;
        }

        if (data[1][0].equals("GB:038753")) {
            assertEquals("Check symbol", "GB:038753", data[1][0]);
            assertEquals("Check security name", "Fidelity Moneybuilder UK Index-#0", data[1][1]);
            assertEquals("Check price", "79.21", data[1][2]);
            assertEquals("Check currency", "GBP", data[1][3]);
            checkedItems++;
        }

        assertEquals("Check that we have asserted against all expected items", 2, checkedItems);

    }
}

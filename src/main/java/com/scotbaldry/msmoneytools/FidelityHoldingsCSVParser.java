package com.scotbaldry.msmoneytools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that is able to parse the CSV files produced by the Holdings & Transactions link within the Fidelity My Accounts page
 */
public class FidelityHoldingsCSVParser {
    private String[] _headerFormat = {"Provider", "Holding", "Income status", "Price per unit", "Date", "Units", "Holding valuation", "Holding currency code", "Reporting valuation", "Reporting currency code"};
    private File _csvFile;
    private MapperParser _mapper;
    private Date _valuationDate;
    private Map<String, String[]> _prices = new HashMap<>();

    public FidelityHoldingsCSVParser(String csvFilename, MapperParser mapper) {
        _csvFile = new File(csvFilename);
        _mapper = mapper;
    }

    public void parse() throws IOException, ParseException {
        String line = "";
        String cvsSplitBy = ",";
        boolean processingBody = false;
        int headerRow = 0;
        int bodyRow = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(_csvFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] columns = line.split(cvsSplitBy);

                if (!processingBody) {
                    // Extract statement date from first role in the file
                    if (headerRow == 0) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                        _valuationDate = dateFormat.parse(columns[0]);
                        headerRow++;
                        continue;
                    }
                    else if (columns.length < 1 || !"Provider".equals(columns[0])) {
                        headerRow++;
                        continue;
                    }
                }

                processingBody = true;

                if (bodyRow == 0) {
                    validateHeader(columns);
                }
                else {
                    _prices.put(columns[1], columns);
                }

                bodyRow++;
            }
        }
    }

    public List<SecurityPrice> getSecurityPrices() throws ParseException {
        List<SecurityPrice> securityPriceList = new ArrayList<>();

        for (String s : _prices.keySet()) {
            SecurityPrice securityPrice;
            String[] columns = _prices.get(s);

            String mappedSymbol = _mapper.getSecurityNameIndex().get(columns[1]).getToSymbol();
            String mappedName = _mapper.getSecurityNameIndex().get(columns[1]).getToSecurityName();

            securityPrice = new SecurityPrice(mappedSymbol,                  // Symbol
                                              mappedName,                    // Security Name
                                              columns[3],                    // Price
                                              columns[7],                    // Currency
                                              convertDate(columns[4]),       // Date
                                              "Price as of close of business");

            securityPriceList.add(securityPrice);
        }

        return securityPriceList;
    }

    public Date getValuationDate() {
        return _valuationDate;
    }

    public int getRowCount() {
        return _prices.keySet().size();
    }

    private boolean validateHeader(String[] columns) {
        if (_headerFormat.length != columns.length) {
            return false;
        }

        for (int i = 0; i < _headerFormat.length; i++) {
            if (!_headerFormat[i].equals(columns[i])) {
                return false;
            }
        }

        return true;
    }

    private Date convertDate(String input) throws ParseException {
        // Dates are stored as DD/MM/YYYY in the incoming file
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(input);
    }
}

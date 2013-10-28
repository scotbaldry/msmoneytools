package com.scotbaldry.msmoneytools;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that is able to parse the CSV files produced by the following webpage:
 *
 *  https://www.fidelity.co.uk/investor/funds/fund-prices/default.page
 *
 */
public class FidelityFundPricesCSVParser {
    private static String[] _headerFormat = {"Fund Name", "Inc/Acc", "Updated", "Buy", "Sell", "Change", "Currency", "Yield%", "Ex Div"};
    private File _csvFile;
    private Map<String, String[]> _prices = new HashMap<>();

    public FidelityFundPricesCSVParser(String csvFilename) {
        _csvFile = new File(csvFilename);
    }

    public static String[] getColumns() {
        return _headerFormat;
    }

    public void parse() {
        String line = "";
        String cvsSplitBy = ",";
        int row = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(_csvFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] columns = line.split(cvsSplitBy);

                if (row == 0) {
                    validateHeader(columns);
                }
                else {
                    // Guard against empty rows in the input file
                    if (!"".equals(columns[0].trim())) {
                        _prices.put(columns[0], columns);
                    }
                }

                row++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String[] getPriceByFundName(String fundName) {
        return _prices.get(fundName);
    }

    public int getRowCount() {
        return _prices.keySet().size();
    }
}

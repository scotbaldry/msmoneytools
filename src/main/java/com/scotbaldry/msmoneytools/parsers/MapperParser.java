package com.scotbaldry.msmoneytools.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapperParser implements IParser {
    private static String[] _headerFormat = {"from symbol", "from security name", "to symbol", "to security name"};

    private Map<String, SecurityMapDetails> _symbolIndex = new HashMap<>();
    private Map<String, SecurityMapDetails> _securityNameIndex = new HashMap<>();
    private List<SecurityMapDetails> _data = new ArrayList<>();

    public MapperParser() {
    }

    public String[] getColumns() {
        return _headerFormat;
    }

    public String[] getHeader() {
        return _headerFormat;
    }

    @Override
    public void parse(File filename) throws Exception {
        String line = "";
        String cvsSplitBy = ",";
        int row = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] columns = line.split(cvsSplitBy);

                if (row == 0) {
                    validateHeader(columns);
                }
                else {
                    // Guard against empty rows in the input file
                    if (!"".equals(columns[0].trim())) {
                        SecurityMapDetails details = new SecurityMapDetails(columns[0], columns[1], columns[2], columns[3]);
                        _symbolIndex.put(details.getFromSymbol(), details);
                        _securityNameIndex.put(details._fromSecurityName, details);
                        _data.add(details);
                    }
                }

                row++;
            }
        }
    }

    public Map<String, SecurityMapDetails> getSymbolIndex() {
        return _symbolIndex;
    }

    public Map<String, SecurityMapDetails> getSecurityNameIndex() {
        return _securityNameIndex;
    }

    /**
     * Method to return all parsed data in a simple 2 dimensional array suitable for
     * rendering in a table or similar data component.
     *
     * @return a 2 dimensional array of Objects (columns x rows)
     */
    @Override
    public Object[][] getData() {
        Object[][] data = new Object[_data.size()][4];

        for (int i = 0; i < _data.size(); i++) {
            data[i][0] = _data.get(i).getFromSymbol();
            data[i][1] = _data.get(i).getFromSecurityName();
            data[i][2] = _data.get(i).getToSymbol();
            data[i][3] = _data.get(i).getToSecurityName();
        }

        return data;
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

    static class SecurityMapDetails {
        private String _fromSymbol;
        private String _fromSecurityName;
        private String _toSymbol;
        private String _toSecurityName;

        SecurityMapDetails(String fromSymbol, String fromSecurityName, String toSymbol, String toSecurityName) {
            _fromSymbol = fromSymbol;
            _fromSecurityName = fromSecurityName;
            _toSymbol = toSymbol;
            _toSecurityName = toSecurityName;
        }

        String getFromSymbol() {
            return _fromSymbol;
        }

        String getFromSecurityName() {
            return _fromSecurityName;
        }

        String getToSymbol() {
            return _toSymbol;
        }

        String getToSecurityName() {
            return _toSecurityName;
        }
    }
}

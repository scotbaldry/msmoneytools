package com.scotbaldry.msmoneytools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapperParser {
    private String[] _headerFormat = {"from symbol", "from security name", "to symbol", "to security name"};
    private File _csvFile;
    private Map<String, SecurityMapDetails> _symbolIndex = new HashMap<>();
    private Map<String, SecurityMapDetails> _securityNameIndex = new HashMap<>();

    public MapperParser(String csvFilename) {
        _csvFile = new File(csvFilename);
    }

    public void parse() throws Exception {
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
                        SecurityMapDetails details = new SecurityMapDetails(columns[0], columns[1], columns[2], columns[3]);
                        _symbolIndex.put(details.getFromSymbol(), details);
                        _securityNameIndex.put(details._fromSecurityName, details);
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

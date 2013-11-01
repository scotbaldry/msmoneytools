package com.scotbaldry.msmoneytools.parsers;

import java.io.File;

public interface IParser {
    String[] getHeader();
    String[] getColumns();
    void parse(File filename) throws Exception;
    Object[][] getData() throws Exception;
}

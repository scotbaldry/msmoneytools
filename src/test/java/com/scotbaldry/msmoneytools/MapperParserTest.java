package com.scotbaldry.msmoneytools;

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MapperParserTest extends TestCase {
    public MapperParserTest(String name) {
        super(name);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MapperParserTest.class);
    }

    public void testValidMappingFile() {
        URL mappingFile = ClassLoader.getSystemResource("fidelity_mappings.csv");
        MapperParser parser = new MapperParser(mappingFile.getFile());
        parser.parse();
        assertEquals("Check number of entries in security name map", 26, parser.getSecurityNameIndex().size());
        assertEquals("Check number of entries in symbol index", 26, parser.getSymbolIndex().size());
    }
}

package com.scotbaldry.msmoneytools;

import java.io.File;
import java.net.URL;

import com.scotbaldry.msmoneytools.parsers.MapperParser;
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

    /**
     * Valid test case where security mappings exists but where there are no symbol mappings. This is because
     * some file formats (such as Fidelity Holdings view) don't provide from symbols.
     */
    public void testValidMappingFileCase1() throws Exception {
        URL mappingFile = ClassLoader.getSystemResource("fidelity_mappings_1.csv");
        MapperParser parser = new MapperParser();
        parser.parse(new File(mappingFile.getFile()));
        assertEquals("Check number of entries in security name map", 26, parser.getSecurityNameIndex().size());
        assertEquals("Check number of entries in symbol index", 1, parser.getSymbolIndex().size());
    }

    public void testValidMappingFileCase2() throws Exception {
        URL mappingFile = ClassLoader.getSystemResource("fidelity_mappings_2.csv");
        MapperParser parser = new MapperParser();
        parser.parse(new File(mappingFile.getFile()));
        assertEquals("Check number of entries in security name map", 26, parser.getSecurityNameIndex().size());
        assertEquals("Check number of entries in symbol index", 24, parser.getSymbolIndex().size());  // Two less than total roles due to duplicate symbol rows (real scenario)
    }
}

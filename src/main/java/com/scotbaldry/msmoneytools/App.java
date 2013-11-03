package com.scotbaldry.msmoneytools;

import com.scotbaldry.msmoneytools.parsers.FidelityHoldingsCSVParser;
import com.scotbaldry.msmoneytools.parsers.MapperParser;
import com.scotbaldry.ofxschema.OFX;
import com.scotbaldry.ofxschema.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class App {
    public App() {
    }

    public static void main(String[] args) {
        App app = new App();
        try {
            app.run();
        }
        catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void run() throws Exception {
        MapperParser mapperParser = new MapperParser();
        mapperParser.parse(new File("c:/develop/fidelity_mappings.csv"));
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser(mapperParser);
        parser.parse(new File("c:/users/scot baldry/downloads/fidelity2holdings.csv"));
        OFXBuilder ofxBuilder = new OFXBuilder(parser.getValuationDate(), parser.getSecurityPrices());

        FileOutputStream fileOutputStream = new FileOutputStream("c:/develop/fidelity2holdings.ofx");
        ofxBuilder.marshallXML(ofxBuilder.buildOFX(), fileOutputStream);
    }
}

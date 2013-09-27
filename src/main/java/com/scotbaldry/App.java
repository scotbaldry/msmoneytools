package com.scotbaldry;

import com.scotbaldry.ofxschema.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class App {
    public App() {
    }

    public static void main(String[] args) {
        App app = new App();
        try {
            app.run();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void run() throws IOException {
        MapperParser mapperParser = new MapperParser("c:/develop/fidelity_mappings.csv");
        mapperParser.parse();
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser("c:/users/scot baldry/downloads/fidelity2holdings.csv", mapperParser);
        parser.parse();
        OFXBuilder ofxBuilder = new OFXBuilder(parser.getSecurityPrices());

        FileOutputStream fileOutputStream = new FileOutputStream("c:/develop/fidelity2holdings.ofx");
        marshallXML(ofxBuilder.buildOFX(), fileOutputStream);
    }

    private void marshallXML(OFX ofx, OutputStream out) {
        try {
            JAXBContext context = JAXBContext.newInstance(OFX.class);
            Marshaller jaxbMarshaller = context.createMarshaller();

            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<OFX> ofxElement = objectFactory.createOFX(ofx);
            JAXBElement<> element = new JAXBElement<> (new QName("http://www.something.com/something","FoodSchema"), .class, ofx);

            ofxElement.setNil(true);

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
            out.write("<?OFX OFXHEADER=\"200\" VERSION=\"200\" SECURITY=\"NONE\" OLDFILEUID=\"NONE\" NEWFILEUID=\"NONE\"?>".getBytes());
            jaxbMarshaller.marshal(ofxElement, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

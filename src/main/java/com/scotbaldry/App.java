package com.scotbaldry;

import com.scotbaldry.ofxschema.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
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
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser("/Users/scot/Downloads/AllHoldings.csv");
        parser.parse();
        OFXBuilder ofxBuilder = new OFXBuilder(parser.getSecurityPrices());
        marshallXML(ofxBuilder.buildOFX());
    }

    private void marshallXML(OFX ofx) {
        try {
            JAXBContext context = JAXBContext.newInstance(OFX.class);
            Marshaller jaxbMarshaller = context.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<OFX> ofxElement = objectFactory.createOFX(ofx);
            jaxbMarshaller.marshal(ofxElement, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}

package com.scotbaldry;

import com.scotbaldry.ofxschema.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
        FidelityHoldingsCSVParser parser = new FidelityHoldingsCSVParser("c:/users/scot baldry/downloads/fidelity2holdings.csv");
        parser.parse();
        OFXBuilder ofxBuilder = new OFXBuilder(parser.getSecurityPrices(), mapperParser);

        FileOutputStream fileOutputStream = new FileOutputStream("c:/develop/fidelity2holdings.ofx");
        marshallXML(ofxBuilder.buildOFX(), fileOutputStream);
    }

    private void marshallXML(OFX ofx, OutputStream out) {
        try {
            JAXBContext context = JAXBContext.newInstance(OFX.class);
            Marshaller jaxbMarshaller = context.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<OFX> ofxElement = objectFactory.createOFX(ofx);
            jaxbMarshaller.marshal(ofxElement, out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}

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
        marshallXML(ofxBuilder.buildOFX(), fileOutputStream);
    }

    /**
     * Sadly since MS Money does not handle any form of namespaces (!!) we have to manipulate the root element to
     * strip the namespace prefix and the xmlns attribute. Very crude way of doing this but not worth starting
     * an XSLT transform...
     *
     * @param xml document to be manipulated
     */
    public static void mungeRootElement(Document xml) {
        Node root = xml.getDocumentElement();
        NodeList rootchildren = root.getChildNodes();
        Element newroot = xml.createElement("OFX");

        for (int i = 0; i < rootchildren.getLength(); i++) {
            newroot.appendChild(rootchildren.item(i).cloneNode(true));
        }

        xml.replaceChild(newroot, root);
    }

    private void marshallXML(OFX ofx, OutputStream out) {
        try {
            StringWriter writer = new StringWriter();

            JAXBContext context = JAXBContext.newInstance(OFX.class);
            Marshaller jaxbMarshaller = context.createMarshaller();
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<OFX> ofxElement = objectFactory.createOFX(ofx);

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            jaxbMarshaller.marshal(ofxElement, writer);

            // Convert to DOM so we can adjust the XML suitable for MS Money Import!
            Document xmlDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(writer.toString())));
            mungeRootElement(xmlDOM);

            // Output the Document
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(xmlDOM);
            out.write("<?OFX OFXHEADER=\"200\" VERSION=\"200\" SECURITY=\"NONE\" OLDFILEUID=\"NONE\" NEWFILEUID=\"NONE\"?>\n".getBytes());
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

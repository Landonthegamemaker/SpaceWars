package util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLUtil {
    private XMLUtil() {}

    public static Document injestXML() {
        File xmlFile = getXmlFile();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc;

        try {
            doc = builder.parse(xmlFile);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return doc;
    }

    public static File getXmlFile() {
        JFileChooser chooser = new JFileChooser();
        File xmlFile = null;

        chooser.setDialogTitle("Choose Ships.xml File Location:");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setSize(400,600);

        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "XML Files", "XML", "xml");
        chooser.setFileFilter(filter);

        chooser.setCurrentDirectory(new File("config"));
        chooser.setSelectedFile(new File("config/ships.xml"));

        int choice = chooser.showOpenDialog(null);
        if(choice == JFileChooser.APPROVE_OPTION) {
            xmlFile = chooser.getSelectedFile();
        }

        if(xmlFile == null) {
            System.out.println("No file selected! Aborting battle program.");
            System.exit(0);
        }

        return xmlFile;
    }

    public static Node getChildNode(Node node, String tagName) {
        if(node.hasChildNodes()) {
            NodeList list = node.getChildNodes();

            for(int i = 0; i < list.getLength(); i++) {
                Node childNode = list.item(i);

                if(childNode.getNodeName().equals(tagName)) return childNode;
            }
        }

        throw new RuntimeException("No child node \"" + tagName + "\" found.");
    }

    public static String getChildNodeText(Node node, String tagName) {
        return getChildNode(node, tagName).getTextContent();
    }
}

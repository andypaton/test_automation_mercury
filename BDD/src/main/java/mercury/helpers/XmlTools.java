package mercury.helpers;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlTools {

    @SuppressWarnings("deprecation")
    public static Document makeXmlDocument(String xml){

        if(isValidXml(xml)) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(IOUtils.toInputStream(xml));
                return doc;
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e.getMessage());
            } catch (SAXException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return null;
    }

    public static String getValueByXpath(Document doc, String xPathExpression) {

        String xPathResult = null;
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile(xPathExpression);
            Object tempNode = expr.evaluate(doc, XPathConstants.NODE);
            if (tempNode != null) {
                xPathResult = (String)expr.evaluate(doc, XPathConstants.STRING);
            }
        } catch (XPathExpressionException e) {

        }

        return xPathResult;
    }

    public static boolean nodeExistsByXPath(Document doc, String xPathExpression) {
        XPathExpression expr;
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            expr = xpath.compile(xPathExpression);
            return (expr.evaluate(doc, XPathConstants.NODE) != null) ? true: false;
        } catch (XPathExpressionException e) {
            return false;
        }
    }

    public static boolean isValidXml(String xml) {
        if (xml==null) {
            return false;
        }
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final InputSource source = new InputSource(new StringReader(xml));
            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            builder.parse(source);
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserConfigurationException e) {
            return false;
        }
        return true;
    }

    public static Document removeWhiteSpace(Document doc) {
        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList nl = null;
        try {
            nl = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException xpe) {
            throw new RuntimeException("Failed remove node from XML", xpe.getCause());
        }

        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            node.getParentNode().removeChild(node);
        }
        return doc;
    }

    public static Node removeComments(Node node) {
        if (node.getNodeType() == Node.COMMENT_NODE) {
            node.getParentNode().removeChild(node);
        } else {
            // check the children recursively
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                removeComments(list.item(i));
            }
        }
        node.normalize();
        return node;
    }
}

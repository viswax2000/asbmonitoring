package com.vcc.asb.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class JAXBUtils {
	
	public <T> T getObjectFromXml(String xml, Class<T> clazz) throws XPathExpressionException, JAXBException {
		
		T t = null;
		
		Document doc = getDocument(xml);
		
		Node n = doc.getDocumentElement();

		Node node1 = null;
		
		if(n.getNodeName().equalsIgnoreCase(clazz.getSimpleName())) {
			node1 = n;
		} else {
			NodeList nl = doc.getDocumentElement().getElementsByTagName(clazz.getSimpleName());
			node1 = nl.item(0);
		}
		
		
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller um = jc.createUnmarshaller();
		Marshaller m = jc.createMarshaller();
		
		JAXBElement<T> qd = um.unmarshal(node1, clazz);

		t = qd.getValue();
		
		StringWriter sw = new StringWriter();
		
		m.marshal(qd, sw);
		
		return t;
	}
	
	public Document getDocument(String xml) {
		Document doc = null;
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	public static void main(String args[]) throws Exception {
		
		JAXBUtils j = new JAXBUtils();
		FileInputStream fis = new FileInputStream("src/main/resources/xml/topic1.xml");
		byte[] b = new byte[fis.available()];
		int read = fis.read(b);
		String s = new String(b);
		com.vcc.asb.config.model.TopicDescription qd = j.getObjectFromXml(s, com.vcc.asb.config.model.TopicDescription.class);
		//System.out.println(qd.getStatus()+","+qd.getEntityAvailabilityStatus());

		//MessageCountDetails countDetails = qd.getCountDetails();
		//Long sizeInBytes = qd.getSizeInBytes();
		//Long currentMsgCount = qd.getMessageCount();

		JAXBContext context = JAXBContext.newInstance(com.vcc.asb.config.model.NamespaceDescription.class);
		Marshaller um = context.createMarshaller();
		StringWriter sw = new StringWriter();
		um.marshal(qd, sw);
		
		System.out.println(sw.toString());
		
		
		//System.out.println("Current Message Count:"+currentMsgCount+", active CountDetails:"+countDetails.getActiveMessageCount());
		//System.out.println("Size in Bytes:"+sizeInBytes);
		

		
	}
	
}

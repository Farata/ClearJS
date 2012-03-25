package com.farata.cdb.annotations.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HQLHelper {

	public static SessionFactoryImplementor factory;

	public static QueryTranslator compileQuery(String query) throws Exception {
		QueryTranslatorFactory translatorFactory = factory.getSettings()
				.getQueryTranslatorFactory();
		QueryTranslator translator = translatorFactory.createQueryTranslator(
				query, query, Collections.EMPTY_MAP,
				(SessionFactoryImplementor) factory);
		translator.compile(factory.getSettings().getQuerySubstitutions(), false);
		return translator;
	}

//	public static SessionFactoryImplementor createSessionFactory() throws FileNotFoundException,
//			ParserConfigurationException, SAXException, IOException {
//		return createSessionFactoryFromDocument(parseConfiguration(null));
//	}
	
	public static SessionFactoryImplementor createSessionFactory(
			String configurationFile) throws FileNotFoundException,
			ParserConfigurationException, SAXException, IOException {
		return createSessionFactoryFromDocument(parseConfiguration(configurationFile));
	}

	public static SessionFactoryImplementor createSessionFactoryFromDocument(
			Document confDocument) {
		AnnotationConfiguration conf = new AnnotationConfiguration();

		NodeList properties = confDocument.getElementsByTagName("property");

		for (int i = 0; i < properties.getLength(); i++) {
			Node item = properties.item(i);
			String value = getAttributeValue(item, "name");
			if (value != null
					&& value.startsWith("current_session_context_class")) {
				item.setNodeValue("thread");
			} else if (value != null
					&& value.startsWith("hibernate.dialect")) {
				item.setNodeValue("org.hibernate.dialect.HSQLDialect");
			} 
			else {
				item.getParentNode().removeChild(item);
			}			
		}
		factory = (SessionFactoryImplementor) conf.configure(confDocument)
				.buildSessionFactory();
		return factory;
	}

	public static Document parseConfiguration(String configurationFile)
			throws ParserConfigurationException, FileNotFoundException,
			SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setFeature("http://xml.org/sax/features/namespaces", false);
		dbf.setFeature("http://xml.org/sax/features/validation", false);
		dbf
				.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
						false);
		dbf
				.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-external-dtd",
						false);
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		InputStream is = null;
		if (configurationFile == null) {
			is = HQLHelper.class.getClassLoader().getResourceAsStream(
					"hibernate.cfg.xml");
		} else {
			is = new FileInputStream(configurationFile);
		}
		InputSource inputSource = new InputSource(is);
		Document document = docBuilder.parse(inputSource);
		return document;
	}

	public static String getAttributeValue(Node item, String name) {
		NamedNodeMap attributes = item.getAttributes();
		Node attr = attributes.getNamedItem(name);
		return attr == null ? null : attr.getTextContent();
	}
}
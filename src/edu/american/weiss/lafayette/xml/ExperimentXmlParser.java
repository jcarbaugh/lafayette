package edu.american.weiss.lafayette.xml;

import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.american.huntsberry.composite.GenericComposite;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.experiment.Experiment;

public class ExperimentXmlParser {

	private SAXReader reader;
	
	public ExperimentXmlParser() {
		reader = new SAXReader();
	}
	
	public Experiment loadExperiment(InputStream is) {
				
		Experiment exp = null;
		
		Document doc = getDocument(is);
		Element root = doc.getRootElement();
		
		exp = loadExperimentClass(root.attributeValue("class"));
		
		Iterator compIt = root.elementIterator("composite");
		while (compIt.hasNext()) {
			
			Element comp = (Element) compIt.next();
			
			GenericComposite gc = new GenericComposite();
			
			Iterator compElemIt = comp.elementIterator("composite-element");
			while (compElemIt.hasNext()) {
				
				Element compElem = (Element) compElemIt.next();
				
				CompositeElement ce = loadCompositeElementClass(compElem.attributeValue("class"));
				
				// set background color
				// set color
				// set shape
				// set actions
				
				Iterator actIt = compElem.elementIterator("action");
				while (actIt.hasNext()) {
					
					
					
				}
				
			}
			
			
			
		}
		
		
		return exp;
		
	}
	
	public Document getDocument(InputStream is) {
		
		Document doc = null;
		
		try {
			doc = reader.read(is);
		} catch (DocumentException de) { }
		
		return doc;
		
	}
	
	private Experiment loadExperimentClass(String className) {
		
		Experiment exp = null;
		
		try {
		
			Class expClass = Class.forName(className);
			Object o = expClass.newInstance();
			
			if (o instanceof Experiment) {
				exp = (Experiment) o;
			} else {
				throw new InstantiationException("------------");
			}
			
		} catch (ClassNotFoundException cnfe) {
		} catch (IllegalAccessException iae) {
		} catch (InstantiationException ie) { }
		
		return exp;
		
	}
	
	private CompositeElement loadCompositeElementClass(String className) {
		
		CompositeElement ce = null;
		
		try {
		
			Class expClass = Class.forName(className);
			Object o = expClass.newInstance();
			
			if (o instanceof Experiment) {
				ce = (CompositeElement) o;
			} else {
				throw new InstantiationException("------------");
			}
			
		} catch (ClassNotFoundException cnfe) {
		} catch (IllegalAccessException iae) {
		} catch (InstantiationException ie) { }
		
		return ce;
		
	}
	
}
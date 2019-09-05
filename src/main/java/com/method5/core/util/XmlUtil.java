package com.method5.core.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XmlUtil
{
	public static <T> T getStringAsObject(String source, Class<T> sourceClass) throws Exception
	{
		javax.xml.bind.JAXBContext context = JAXBContext.newInstance(sourceClass);
		
		InputStream inputStream = StringUtil.convertToInputStream(source);
		
		Unmarshaller unmarshaller = context.createUnmarshaller();
	    Object result = unmarshaller.unmarshal(inputStream);
	    
	    return sourceClass.cast(result);
	}
	
	public static String getObjectAsString(Object source) throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(source.getClass());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try
		{
			Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    m.marshal(source, baos);
		    
			return new String(baos.toByteArray());
		}
		finally
		{
			ClosureUtil.close(baos);
		}
	}
	
	public static String documentToXml(Document doc) throws TransformerFactoryConfigurationError, TransformerException
	{
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String xmlString = result.getWriter().toString();
		
		return xmlString;
	}
}
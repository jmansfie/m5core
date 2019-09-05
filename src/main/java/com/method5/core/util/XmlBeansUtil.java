package com.method5.core.util;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.method5.core.exception.ValidationException;


public class XmlBeansUtil
{
	public static void validate(XmlObject obj) throws ValidationException
	{
		ArrayList<Object> validationErrors = new ArrayList<Object>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);
		
		StringBuilder errorMsg = new StringBuilder();
		
		boolean isValid = obj.validate(validationOptions);

		if (!isValid)
		{
		    Iterator<Object> iter = validationErrors.iterator();
		    while (iter.hasNext())
		    {
		        errorMsg.append(iter.next()).append("\n");
		    }
		    throw new ValidationException("Message validation failed: " + errorMsg);
		}
	}
	
	public static XmlObject clone(XmlObject x) throws XmlException
	{
		String xml = x.xmlText();
		return XmlObject.Factory.parse(xml);
	}
	
	public static void localizeXmlFragment(XmlObject x)
	{
	     String s;
	     XmlCursor c = x.newCursor();
	     c.toNextToken();
	     while (c.hasNextToken())
	     {
	         if (c.isNamespace())
	             c.removeXml();
	         else
	         {
	             if (c.isStart())
	             {
	                 s = c.getName().getLocalPart();
	                 c.setName(new QName(s));
	             }
	             c.toNextToken();
	         }
	     }
	     c.dispose();
	 }
}

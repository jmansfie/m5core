package com.method5.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import com.method5.core.exception.CoreException;

public class StringUtil
{
	public static final String KEY_SEPERATOR = "_KEY_";
	public static final String ITEM_SEPERATOR = "_ITEM_";
	
	public static boolean isNullOrEmpty(String str)
	{
		return (str == null || str.trim().compareTo("") == 0);
	}
	
	public static String generateKey(String key, String ... str)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(key).append(KEY_SEPERATOR);
		for(String temp : str)
		{
			sb.append(temp).append(ITEM_SEPERATOR);
		}
		return sb.toString();
	}
	
	public static String concat(List<String> strings)
	{
		StringBuilder sb = new StringBuilder();

		for (String string : strings)
		{
			sb.append(string);
		}

		return sb.toString();
	}

	public static String concat(Object... objs)
	{
		StringBuilder sb = new StringBuilder();

		for (Object obj : objs)
		{
			sb.append(obj);
		}

		return sb.toString();
	}

	public static String removeNonDigits(String text)
	{
		return text.replaceAll("[^(0-9]*", "");
	}

	public static InputStream convertToInputStream(String text)
	{
		if(text == null)
		{
			return null;
		}
		
		return new ByteArrayInputStream(text.getBytes());
	}
	
	public static String convertStreamToString(InputStream is) throws IOException
	{
		if (is != null)
		{
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];

			try
			{
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;

				while ((n = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, n);
				}
			}
			finally
			{
				ClosureUtil.close(is);
			}

			return writer.toString();
		}
		else
		{
			return "";
		}
	}
	
	public static String removeInvalidCharacters(String str, String encoding) throws CoreException
	{
		Guard.notNullOrEmpty("encoding missing", encoding);
		
		if(StringUtil.isNullOrEmpty(str))
			return str;
		
		return str.replaceAll("[^\\x20-\\x7e]", "");
	}
}
package com.method5.core.messaging;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ResourceUtils;

import com.method5.core.messaging.MessageFileWriter;
import com.method5.core.messaging.MessageFileWriter.FileType;
import com.method5.core.util.StringUtil;

public class MessageFileWriterTest
{
	private static final Logger LOG = LogManager.getLogger(MessageFileWriterTest.class);
	
	private static MessageFileWriter messageWriter = null;
	
	private static String fileName = null;
	
	@BeforeClass
	public static void init() throws Exception
	{
		ClassPathXmlApplicationContext context = null;
		try
		{
			context = new ClassPathXmlApplicationContext("spring/messageFileWriter.xml");
			
			messageWriter = context.getBean(MessageFileWriter.class);
			
			fileName = messageWriter.getFileName(FileType.IN.toString(), null);
		}
		finally
		{
			if(context != null)
				context.close();
		}
	}
	
	@AfterClass
	public static void destroy()
	{
		if(fileName != null)
		{
			try
			{
				File file = ResourceUtils.getFile(fileName);
				
				if(file.exists())
					file.delete();
			}
			catch(FileNotFoundException e)
			{
				LOG.info(StringUtil.concat(fileName, " not found."));
			}
		}
	}

	@Test
	public void canWriteToFile() throws Exception
	{
		messageWriter.writeToFile(FileType.IN, "test \n\n messa\nge");

		messageWriter.writeToFile(FileType.IN, "abc");
		
		File file = ResourceUtils.getFile(fileName);
		String messageFile1 = FileUtils.readFileToString(file);
			
		Assert.assertEquals("test    messa ge\nabc\n", messageFile1);
	}
}
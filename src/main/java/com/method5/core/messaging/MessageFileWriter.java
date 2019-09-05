package com.method5.core.messaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.method5.core.exception.CoreException;
import com.method5.core.log.Log;
import com.method5.core.util.ClosureUtil;
import com.method5.core.util.Guard;
import com.method5.core.util.StringUtil;

@Component
public class MessageFileWriter
{
	public enum FileType {
		IN, OUT, ERR;
	}

	private final static String DATE_FORMAT = "yyyyMMdd";
	private final static String FILENAME_DATE_PATTERN = "\\%date\\%";
	private final static String FILENAME_EXTRA_PATTERN = "\\%extra\\%";
	
	@Log
	private Logger LOG;
	
	@Resource
	private Map<String, String> messageFiles;
	
	@PostConstruct
	public void initialize()
	{
		Guard.notNull("messageFiles", "messageFiles");
	}
	
	public void writeToFile(FileType type, String message) throws CoreException
	{
		Guard.notNull(type, "type");
		writeToFile(type.toString(), message, null);
	}
	
	public void writeToFile(FileType type, String message, String extra) throws CoreException
	{
		Guard.notNull(type, "type");
		writeToFile(type.toString(), message, extra);
	}
	
	public void writeToFile(final String type, String message) throws CoreException
	{
		writeToFile(type, message, null);
	}
	
	public void writeToFile(final String type, String message, String extra) throws CoreException
	{
		Guard.notNullOrEmpty(type, "type");
		Guard.notNullOrEmpty(message, "message");
		
		if(!messageFiles.containsKey(type.toString()))
			throw new CoreException(StringUtil.concat("Unable to find file name for type: ", type));
		
		try
		{
			String newMessage = formatMessage(message);
				
			String fileName = getFileName(type, extra);
				
			write(fileName, newMessage);
		}
		catch(Exception e)
		{
			LOG.error("Error writing to message file " + messageFiles.get(type.toString()), e);		
			throw new CoreException("Error writing to file", e);
		}
	}
	
	private synchronized void write(String fileName, String message) throws IOException
	{
		Guard.notNullOrEmpty(fileName, "fileName");
		Guard.notNull(message, "message");
		
		if(fileName.contains(File.separator))
		{
			String path = fileName.substring(0, fileName.lastIndexOf(File.separator));
			
			File logDirectory = new File(path);
			try
			{
				FileUtils.forceMkdir(logDirectory);
			}
			catch(Exception e)
			{
				LOG.error(StringUtil.concat("Unable to create directory: ", path));
			}
		}
		
		Writer out = null;
		try
		{
			 out = new OutputStreamWriter(new FileOutputStream(fileName, true));
			 out.write(StringUtil.concat(message, "\n"));
		}
		finally
		{
			ClosureUtil.close(out);
		}
	}

	String formatMessage(String message)
	{
		Guard.notNull(message, "message");
		
		message = message.replace("\n"," ");
		message = message.replace("\r", " ");
		
		return message;
	}

	String getFileName(String type, String extra) throws CoreException
	{
		Guard.notNullOrEmpty(type, "type");
		
		if(!messageFiles.containsKey(type.toString()))
			throw new CoreException(StringUtil.concat("Unable to find file name for type: ", type));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			
		String fileName = messageFiles.get(type).replaceAll(FILENAME_DATE_PATTERN, dateFormat.format(new Date()));
		
		if(extra != null)
			fileName = fileName.replaceAll(FILENAME_EXTRA_PATTERN, extra);
			
		return fileName;
	}

	public Map<String, String> getMessageFiles() {
		return messageFiles;
	}

	public void setMessageFiles(Map<String, String> messageFiles) {
		this.messageFiles = messageFiles;
	}
}

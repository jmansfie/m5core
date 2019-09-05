package com.method5.core.util;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class StringUtilTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(StringUtilTest.class);
	
	@Test
	public void canremoveInvalidCharacters() throws Exception
	{
		Resource fileWithInvalidCharacters = new ClassPathResource("com/method5/core/util/StringUtilTest.txt");
		
		String invalidCharString = IOUtils.toString(fileWithInvalidCharacters.getInputStream());
		
		String result = StringUtil.removeInvalidCharacters(invalidCharString, "UTF-8");

		Assert.assertEquals("testtest", result);
	}
}
package com.method5.core.util;

import org.apache.commons.lang3.SystemUtils;

public class EnvironmentUtil
{
	public static boolean isWindows()
	{
		return SystemUtils.IS_OS_WINDOWS;
	}
	
	public static String getOsVersion()
	{
		return System.getProperty("os.name");
	}
}
package com.method5.core.util;

public class NumberUtil
{
	public static boolean isInteger(String t)
	{
		try
		{
			Integer.parseInt(t);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}

package com.method5.core.util;

import java.util.Collection;

public class Guard
{
	public static void notNull(Object obj, String name)
	{
		if(obj == null)
		{
			throw new RuntimeException(StringUtil.concat(name, " is null"));
		}
	}
	
	public static void notNullOrEmpty(String string, String name)
	{
		if(string == null || string.length() == 0)
		{
			throw new RuntimeException(StringUtil.concat(name, " is nullOrEmpty"));
		}
	}
	
	public static void notNullOrEmpty(Collection<?> collection, String name)
	{
		if(collection == null || collection.size() == 0)
		{
			throw new RuntimeException(StringUtil.concat(name, " is nullOrEmpty"));
		}
	}
	
	public static void notNullOrEmpty(Object[] array, String name)
	{
		if(array == null || array.length == 0)
		{
			throw new RuntimeException(StringUtil.concat(name, " is nullOrEmpty"));
		}
	}
	
	public static void isTrueDependency(boolean dependency, String name)
	{
		if(!dependency)
		{
			throw new RuntimeException(StringUtil.concat(name, " is false"));
		}
	}
	
	@Deprecated
	public static void notNull(Object obj)
	{
		if(obj == null)
		{
			throw new RuntimeException("Tested object is null");
		}
	}
	
	@Deprecated
	public static void notNullOrEmpty(String string)
	{
		if(string == null || string.length() == 0)
		{
			throw new RuntimeException("Tested string is nullOrEmpty");
		}
	}
	
	@Deprecated
	public static void notNullOrEmpty(Collection<?> collection)
	{
		if(collection == null || collection.size() == 0)
		{
			throw new RuntimeException("Tested collection is nullOrEmpty");
		}
	}
	
	@Deprecated
	public static void notNullOrEmpty(Object[] array)
	{
		if(array == null || array.length == 0)
		{
			throw new RuntimeException("Tested array is nullOrEmpty");
		}
	}
}
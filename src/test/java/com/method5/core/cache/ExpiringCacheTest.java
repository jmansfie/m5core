package com.method5.core.cache;

import junit.framework.Assert;
import org.junit.Test;

import com.method5.core.util.RandomUtil;

public class ExpiringCacheTest
{
	private static String KEY1 = RandomUtil.getRandomString(10);
	private static String KEY2 = RandomUtil.getRandomString(10);
	
	@Test
	public void canGetCachedData() throws Exception
	{
		ExpiringCache<String> expiringCache = null;
		
		try
		{
			expiringCache = new ExpiringCache<String>(60);
			
			expiringCache.put(KEY1, "test");
			
			expiringCache.put(KEY2, "test2");
			
			Assert.assertEquals(expiringCache.get(KEY2), "test2");
			
			expiringCache.put(KEY2, "test2-updated");
			
			Assert.assertNotSame(expiringCache.get(KEY2), "test2");
			
			Assert.assertEquals(expiringCache.get(KEY2), "test2-updated");

			Assert.assertEquals(expiringCache.get(KEY1), "test");
		}
		finally
		{
			expiringCache.destroy();
		}
	}
	
	@Test
	public void canExpireItems() throws Exception
	{
		ExpiringCache<Object> expiringCache = null;
		
		try
		{
			expiringCache = new ExpiringCache<Object>(1);
		
			expiringCache.put(KEY1, new Object());
	
			Thread.sleep(1100);
			
			Assert.assertEquals(expiringCache.get(KEY1), null);
		}
		finally
		{
			expiringCache.destroy();
		}
	}
}
package com.method5.core.util;

import java.util.concurrent.Callable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class RetryUtil {
	private static final Logger LOG = LogManager.getLogger(RetryUtil.class);
	
	public static <T> T retryIfNull(Callable<T> callable, int maxRetries, long cooldown) {
		try {
			T t = callable.call();
			
			// if value is good or we burnt through retries
			if(t != null || maxRetries <= 0)
				return t;
			
			Thread.sleep(cooldown);
			return retryIfNull(callable, maxRetries - 1, cooldown);
		}
		catch(Exception e) {
			LOG.error("Failed to retryIfNull", e);
			return null;
		}
	}
}
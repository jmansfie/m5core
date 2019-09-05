package com.method5.core.util;

import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.junit.Test;

public class RetryUtilTest {
	@Test
	public void canRetryOnFailure() {
		final Counter counter = new Counter();
		
		RetryUtil.retryIfNull(new Callable<Object>(){
			@Override
			public Object call() throws Exception {
				counter.increment();
				
				// purposely returning null
				return null;
			}
		}, 3, 0);
		
		// should be first execution + number of retries (ie 1 + 3 = 4)
		Assert.assertEquals(4, counter.getValue());
	}
	
	@Test
	public void canReturnOnSuccess() {
		final Counter counter = new Counter();
		
		RetryUtil.retryIfNull(new Callable<Object>(){
			@Override
			public Object call() throws Exception {
				counter.increment();
				return new Object();
			}
		}, 3, 0);
		
		// should return immediately on a good value
		Assert.assertEquals(1, counter.getValue());
	}
	
	@Test
	public void canReturnPartwayThough() {
		final Counter counter = new Counter();
		
		RetryUtil.retryIfNull(new Callable<Object>(){
			@Override
			public Object call() throws Exception {
				counter.increment();
				
				if(counter.getValue() < 2)
					return null;
				
				return new Object();
			}
		}, 3, 0);
		
		// should return after executing twice, even through 2 more executions were allowable
		Assert.assertEquals(2, counter.getValue());
	}
	
	// needed to allow for use within an anonymous class
	class Counter {
		private int value = 0;
		
		public void increment() {
			value++;
		}
		
		public int getValue() {
			return value;
		}
	}
}
package com.method5.core.job;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LockingExecutor
{
	private final ThreadPoolExecutor threadPool;
	private final ArrayBlockingQueue<Runnable> queue;

	private final ReentrantLock lock = new ReentrantLock();
	
	private static final int SLEEP_TIME_ON_FULL = 100;
	private static final Logger LOG = LogManager.getLogger(LockingExecutor.class);
	
	public LockingExecutor(int poolSize, int maxPoolSize, int queueCapacity, int keepAliveTimeInSeconds)
	{
		queue = new ArrayBlockingQueue<Runnable>(queueCapacity);
		threadPool = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, keepAliveTimeInSeconds, TimeUnit.SECONDS, queue);
		
		for(int i = 0; i < poolSize; i++)
		{
			threadPool.prestartCoreThread();
		}
		
		threadPool.allowCoreThreadTimeOut(true);
	}

	public void execute(Runnable task)
	{
		lock.lock();
		
		try
		{
			while(queue.remainingCapacity() < 1)
			{
				sleep();
			}
			
			threadPool.execute(task);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public <T> Future<T> submit(Callable<T> task)
	{
		lock.lock();
		
		try
		{
			while(queue.remainingCapacity() < 1)
			{
				sleep();
			}
			
			return threadPool.submit(task);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	private void sleep()
	{
		try
		{
			Thread.sleep(SLEEP_TIME_ON_FULL);
		}
		catch (InterruptedException e)
		{
			LOG.error("Failed to sleep", e);
		}
	}
	
	@PreDestroy
	public void shutDown()
	{
		threadPool.shutdown();
	}
}
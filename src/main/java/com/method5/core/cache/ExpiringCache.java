package com.method5.core.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExpiringCache<T>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ExpiringCache.class);
	
	@Value("${cache.defaultExpire}")
	private long defaultExpire;
	
	private Map<String, T> objects;
	private Map<String, Long> expire;
	private ExecutorService threads;
	
	public ExpiringCache()
	{
		this(1000);
	}
	
	public ExpiringCache(long defaultExpire)
	{
		this.defaultExpire = defaultExpire;
		init();
	}

	@PostConstruct
	public void init()
	{
		this.objects = Collections.synchronizedMap(new HashMap<String, T>());
		this.expire = Collections.synchronizedMap(new HashMap<String, Long>());
		this.threads = Executors.newFixedThreadPool(256);
		Executors.newScheduledThreadPool(2).scheduleWithFixedDelay(this.removeExpired(), this.defaultExpire / 2, this.defaultExpire, TimeUnit.MILLISECONDS);
	}
	
	@PreDestroy
	public void destroy()
	{
		threads.shutdownNow();
	}

	private synchronized final Runnable removeExpired()
	{
		return new Runnable()
		{
			public void run()
			{
				for (final String name : expire.keySet())
				{
					if (System.currentTimeMillis() > expire.get(name))
					{
						threads.execute(createRemoveRunnable(name));
					}
				}
			}
		};
	}

	private synchronized final Runnable createRemoveRunnable(final String name)
	{
		return new Runnable()
		{
			public void run()
			{
				objects.remove(name);
				expire.remove(name);
			}
		};
	}

	public long getExpire()
	{
		return this.defaultExpire;
	}

	public synchronized void put(final String name, final T obj)
	{
		this.put(name, obj, this.defaultExpire);
	}

	public synchronized void put(final String name, final T obj, final long expireTime)
	{
		this.objects.put(name, obj);
		this.expire.put(name, System.currentTimeMillis() + expireTime);
	}

	public synchronized T get(final String name)
	{
		final Long expireTime = this.expire.get(name);
		
		if (expireTime == null) return null;
		
		if (System.currentTimeMillis() > expireTime)
		{
			this.threads.execute(this.createRemoveRunnable(name));
			return null;
		}
		
		return this.objects.get(name);
	}
	
	public synchronized int size()
	{
		if(this.objects == null)
			return 0;
		
		return this.objects.size();
	}

	@SuppressWarnings("unchecked")
	public synchronized <R extends T> R get(final String name, final Class<R> type)
	{
		return (R) this.get(name);
	}

	public long getDefaultExpire() {
		return defaultExpire;
	}

	public void setDefaultExpire(long defaultExpire) {
		this.defaultExpire = defaultExpire;
	}
}
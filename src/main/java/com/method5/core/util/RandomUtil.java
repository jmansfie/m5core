package com.method5.core.util;

import java.security.SecureRandom;
import java.util.Date;

public class RandomUtil
{
	private static final long EARLY_DATE = 315464400711L; // Mon Dec 31 00:00:00 EST 1979
	private static final long LATER_DATE = 1577768400381L; // Tue Dec 31 00:00:00 EST 2019

	public static String getRandomString(int length)
	{
		String result = "";

		for (int i = 0; i < length; i++)
		{
			result += getRandomCharacter();
		}

		return result;
	}

	public static char getRandomCharacter()
	{
		int ranch = (int) (Math.random() * 62);

		if (ranch < 10)
		{
			ranch += 48; // (48-0)=48
		}
		else if (ranch < 36)
		{
			ranch += 55; // (65-10)=55
		}
		else
		{
			ranch += 61; // (97-36)=61
		}

		return (char) ranch;
	}

	public static int getRandomInt()
	{
		SecureRandom random = new SecureRandom();
		return random.nextInt();
	}
	
	public static long getRandomLong()
	{
		SecureRandom random = new SecureRandom();
		return random.nextLong();
	}

	public static double getRandomDouble()
	{
		SecureRandom random = new SecureRandom();
		return random.nextDouble();
	}
	
	public static Date getRandomDate()
	{
		long result = getRandomLong();
		result -= EARLY_DATE;
		result %= LATER_DATE - EARLY_DATE;
		result += EARLY_DATE;
		
		return new Date(result);
	}
	
	public static boolean getRandomBoolean()
	{
		SecureRandom random = new SecureRandom();
		return random.nextBoolean();
	}
}
package com.method5.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class NetworkUtil
{
	private static final Logger LOG = LogManager.getLogger(NetworkUtil.class);
	
	public static String getHostname()
	{
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		}
		catch(Exception e)
		{
			LOG.error("Failed to determine system hostname", e);
		}
		return null;
	}
	
	public static String getIpAddress()
	{
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		}
		catch(Exception e)
		{
			LOG.error("Failed to determine ip address", e);
		}
		return null;
	}

	public static String getMacAddress()
	{
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			 
			NetworkInterface network = NetworkInterface.getByInetAddress(addr);
	 
			byte[] mac = network.getHardwareAddress();
	 
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			return sb.toString();
		}
		catch(Exception e)
		{
			LOG.error("Failed to determine mac address", e);
		}
		return null;
	}
}

package com.method5.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClosureUtil
{
	private static final Logger LOG = Logger.getLogger(ClosureUtil.class);

	public static void close(Closeable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
			}
			catch (IOException e)
			{
				LOG.error("Failed to close Closable", e);
			}
		}
	}

	public static void close(Connection conn)
	{
		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				LOG.error("Failed to close Connection", e);
			}
		}
	}

	public static void close(ClassPathXmlApplicationContext context)
	{
		if (context != null)
		{
			try
			{
				context.close();
			}
			catch (Exception e)
			{
				LOG.error("Failed to close ClassPathXmlApplicationContext", e);
			}
		}
	}

	public static void close(ResultSet rs)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (Exception e)
			{
				LOG.error("Failed to close ResultSet", e);
			}
		}
	}

	public static void close(Statement statement)
	{
		if (statement != null)
		{
			try
			{
				statement.close();
			}
			catch (Exception e)
			{
				LOG.error("Failed to close Statement", e);
			}
		}
	}
}
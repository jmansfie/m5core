package com.method5.core.exception;

public class DatabaseException extends CoreException
{
	private static final long serialVersionUID = 2206888758637411947L;

	public DatabaseException(String e)
	{
		super(e);
	}

	public DatabaseException(String string, Exception e) {
		super(string ,e);
	}
}

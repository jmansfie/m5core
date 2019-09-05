package com.method5.core.exception;

public class CoreException extends Exception
{
	private static final long serialVersionUID = 2206888758637411947L;

	public CoreException(String e)
	{
		super(e);
	}

	public CoreException(String string, Exception e) {
		super(string ,e);
	}
}

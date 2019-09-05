package com.method5.core.exception;

public class ValidationException extends CoreException
{
	private static final long serialVersionUID = 2206888758637411947L;

	public ValidationException(String e)
	{
		super(e);
	}

	public ValidationException(String string, Exception e) {
		super(string ,e);
	}
}

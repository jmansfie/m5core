package com.method5.core.exception;

public class ConversionException extends CoreException
{
	private static final long serialVersionUID = 2206888758637411947L;

	public ConversionException(String e)
	{
		super(e);
	}

	public ConversionException(String string, Exception e) {
		super(string ,e);
	}
}

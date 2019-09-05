package com.method5.core.exception;

public class MessagingException extends CoreException
{
	private static final long serialVersionUID = 2206888758637411947L;

	public MessagingException(String e)
	{
		super(e);
	}

	public MessagingException(String string, Exception e) {
		super(string ,e);
	}
}

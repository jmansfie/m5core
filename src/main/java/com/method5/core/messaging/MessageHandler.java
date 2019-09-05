package com.method5.core.messaging;

import com.method5.core.exception.MessagingException;

public interface MessageHandler<T>
{
	public void handleMessage(T message) throws MessagingException;
}

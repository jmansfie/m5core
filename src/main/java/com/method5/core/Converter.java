package com.method5.core;

public interface Converter<T, S>
{
	public S convert(T message) throws Exception;

}

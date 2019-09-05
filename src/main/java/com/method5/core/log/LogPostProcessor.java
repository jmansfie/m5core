package com.method5.core.log;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class LogPostProcessor implements BeanPostProcessor
{
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
    	return bean;
    }

    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException
    {
    	ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback()
    	{
    		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException
    		{
    			if (field.getAnnotation(Log.class) != null)
    			{
    				ReflectionUtils.makeAccessible(field);
    				
    				Logger log = Logger.getLogger(bean.getClass());
    				field.set(bean, log);
    			}
    		}
    	});
    	return bean;
    }
}

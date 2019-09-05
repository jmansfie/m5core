package com.method5.core.config;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.method5.core.enums.EnvironmentEnum;
import com.method5.core.enums.RegionEnum;
import com.method5.core.util.Guard;
import com.method5.core.util.StringUtil;

public class PerHostPropertiesConfigurer extends PropertyPlaceholderConfigurer
{
	private static final Logger LOG = Logger.getLogger(PerHostPropertiesConfigurer.class);
	
	public static final String ENVIRONMENT_SYS_PROPERTY = "env";
	public static final String REGION_SYS_PROPERTY = "region";

	private String file;
	private List<String> files;
	
	private boolean insertIntoSystemProperties = false;
	
	public static String getEnvironment()
	{
		String env = System.getProperty(ENVIRONMENT_SYS_PROPERTY);
		
		if (StringUtils.isBlank(env))
		{
			env = EnvironmentEnum.LOCAL.toString();
		}
		
		return env;
	}
	
	public static String getRegion()
	{
		String region = System.getProperty(REGION_SYS_PROPERTY);

		if (StringUtils.isBlank(region))
		{
			region = RegionEnum.TOR.toString();
		}
		
		return region;
	}

	public void loadProperties(Properties properties) throws IOException
	{
		setConfigLocations();

		super.loadProperties(properties);
	}

	public void setConfigLocations() throws IOException
	{
		assert (file != null || (files != null && files.size() > 0));
		assert (!(file != null && files != null && files.size() > 0));
		
		if(files == null)
		{
			files = new ArrayList<String>();
			files.add("classpath:" + file);
		}
		
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		
		Resource[] resources = new Resource[files.size()];
		
		for(int i = 0; i < files.size(); i++)
		{
			String realConfigLocation = getRealConfigName(files.get(i));
			
			LOG.info(StringUtil.concat("Application config: ", realConfigLocation));
			
			String decryptedAppConfig = "";
			
			try
			{
				Resource appConfigResource = resourceLoader.getResource(realConfigLocation);
				String appConfigAsString = StringUtil.convertStreamToString(appConfigResource.getInputStream());
				
				decryptedAppConfig = appConfigAsString;
//				decryptedAppConfig = ConfigDecryptor.decrypt(appConfigAsString);
			}
			catch (Exception e)
			{
				throw new IOException("Failed to decrypt app config.", e);
			}

			ByteArrayResource decryptedAppConfigResource = new ByteArrayResource(decryptedAppConfig.getBytes())
			{
				@Override
				public String getFilename()
				{
					return "";
				}
			};
			
			resources[i] = decryptedAppConfigResource;
		}

		if (resources.length == 1)
		{
			super.setLocation(resources[0]);
		}
		else
		{
			super.setLocations(resources);
		}
	}

	private String getRealConfigName(String fileName)
	{
		Guard.notNullOrEmpty("file name is missing", fileName);

		String env = getEnvironment();
		String region = getRegion();
		
		LOG.info("Environment is: " + env);
		LOG.info("Region is: " + region);

		String location = fileName.replaceAll("\\{ENV\\}", env);
		location = location.replaceAll("\\{REGION\\}", region);
		
		if(location.contains("{HOSTNAME}"))
		{
			try
			{
				InetAddress addr = InetAddress.getLocalHost();
			    String hostname = addr.getHostName().toUpperCase();
			    
			    LOG.info("Hostname is: " + hostname);
			    
			    location = location.replaceAll("\\{HOSTNAME\\}", hostname);
			}
			catch(Exception e)
			{
				LOG.error("Error retrieving hostname.", e);
			}
		}
				
		return location;
	}

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException
	{
		super.processProperties(beanFactoryToProcess, props);

		if (insertIntoSystemProperties)
		{
			for (Object key : props.keySet())
			{
				String keyStr = key.toString();
				String value = props.getProperty(keyStr);
				System.getProperties().put(keyStr, value);
			}
		}
	}

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public boolean isInsertIntoSystemProperties()
	{
		return insertIntoSystemProperties;
	}

	public void setInsertIntoSystemProperties(boolean insertIntoSystemProperties)
	{
		this.insertIntoSystemProperties = insertIntoSystemProperties;
	}

	public List<String> getFiles()
	{
		return files;
	}

	public void setFiles(List<String> files)
	{
		this.files = files;
	}
}
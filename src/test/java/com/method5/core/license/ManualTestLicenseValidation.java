package com.method5.core.license;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ManualTestLicenseValidation
{
	@Autowired
	private LicenseValidator licenseValidator;
	
	private static final Logger LOG = LogManager.getLogger(ManualTestLicenseValidation.class);

	public static void main(String[] args)
	{
		new ClassPathXmlApplicationContext("spring/license.xml");
	}
	
	@PostConstruct
	public void performTest()
	{
		LOG.info(licenseValidator.isLicenseValid());
	}
}

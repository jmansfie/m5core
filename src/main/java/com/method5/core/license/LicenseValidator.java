package com.method5.core.license;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.method5.core.exception.CoreException;
import com.method5.core.util.ClosureUtil;
import com.method5.core.util.EncryptionUtil;
import com.method5.core.util.Guard;
import com.method5.core.util.NetworkUtil;
import com.method5.core.util.RandomUtil;
import com.method5.core.util.StringUtil;

@Service
public final class LicenseValidator
{
	private static final Logger LOG = LogManager.getLogger(LicenseValidator.class);

	@Value("${method5.license.server}")
	private String licenseServer;

	@Value("${method5.license.serverCertificate}")
	private String licenseServerCertificate;

	@Value("${method5.license.applicationKey}")
	private String applicationKey;

	@Value("${method5.license.licenseNumber}")
	private String licenseNumber;
	
	private boolean initialized = false;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
			
	@PostConstruct
	public void init()
	{
		LOG.info(StringUtil.concat("Method5 Application Key: ", applicationKey));
		LOG.info(StringUtil.concat("Method5 License Number: ", licenseNumber));
		
		/* check license once per day and on start-up */
		scheduler.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				licenseValidation();
			}
		}, 0, 86400, TimeUnit.SECONDS);
		
		initialized = true;
	}
	
	public void licenseValidation()
	{
		if (!isLicenseValid())
		{
			LOG.fatal("Not authorized. Please contact Method5 Inc.");
			System.exit(-1);
		}
	}

	public boolean isLicenseValid()
	{
		Guard.notNullOrEmpty(licenseServer, "licenseServer");

		LOG.info("Validating software license");
		DefaultHttpClient httpClient = null;

		try
		{
			httpClient = new DefaultHttpClient();

			HttpPost request = new HttpPost(licenseServer);

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("applicationKey", applicationKey));
			postParameters.add(new BasicNameValuePair("licenseNumber", licenseNumber));

			String seed = RandomUtil.getRandomString(10);

			postParameters.add(new BasicNameValuePair("seed", seed));
			postParameters.add(new BasicNameValuePair("hostname", NetworkUtil.getHostname()));
			postParameters.add(new BasicNameValuePair("ipAddress", NetworkUtil.getIpAddress()));
			postParameters.add(new BasicNameValuePair("macAddress", NetworkUtil.getMacAddress()));

			request.setEntity(new UrlEncodedFormEntity(postParameters));

			HttpResponse response = httpClient.execute(request);

			if (response.getStatusLine().getStatusCode() != 200)
			{
				LOG.error("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
				return false;
			}

			BufferedReader br = null;
			InputStreamReader is = null;
			
			try
			{
				is = new InputStreamReader((response.getEntity().getContent()));
				br = new BufferedReader(is);

				StringBuilder output = new StringBuilder();
				String line;
				
				while ((line = br.readLine()) != null)
				{
					output.append(line.trim());
				}

				String serverResponse = output.toString();
				LOG.info("License server response: " + serverResponse);

				return validateResponse(serverResponse, seed);
			}
			finally
			{
				ClosureUtil.close(is);
				ClosureUtil.close(br);
			}
		}
		catch (Exception e)
		{
			LOG.error("Error occured contacting license server", e);
		}
		finally
		{
			httpClient.getConnectionManager().shutdown();
		}
		
		LOG.warn("License is not valid. Please contact Method5 Inc.");
		return false;
	}

	private boolean validateResponse(String output, String seed) throws CoreException
	{
		String decoded = EncryptionUtil.decryptWithCertificate(licenseServerCertificate, output);
		String expected = StringUtil.concat(LicenseServerResponseEnum.VALID.getCode(), "|", seed);

		if (decoded != null && decoded.compareTo(expected) == 0)
		{
			LOG.info("License is valid");
			return true;
		}
		
		LOG.warn("License is not valid. Please contact Method5 Inc.");
		return false;
	}

	public String getApplicationKey()
	{
		return applicationKey;
	}

	public void setApplicationKey(String applicationKey)
	{
		this.applicationKey = applicationKey;
	}

	public String getLicenseNumber()
	{
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber)
	{
		this.licenseNumber = licenseNumber;
	}

	public String getLicenseServer()
	{
		return licenseServer;
	}

	public void setLicenseServer(String licenseServer)
	{
		this.licenseServer = licenseServer;
	}

	public String getLicenseServerCertificate()
	{
		return licenseServerCertificate;
	}

	public void setLicenseServerCertificate(String licenseServerCertificate)
	{
		this.licenseServerCertificate = licenseServerCertificate;
	}

	public boolean isInitialized() {
		return initialized;
	}
}
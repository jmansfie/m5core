package com.method5.core.util;

import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.method5.core.exception.CoreException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public final class EncryptionUtil
{
	private static final Logger LOG = LogManager.getLogger(EncryptionUtil.class);

	public static int hex2decimal(String s)
	{
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
	
	public static String decryptWithCertificate(String certificatePath, String cipher) throws CoreException
	{	
		BASE64Decoder decoder = new BASE64Decoder();
		CertificateFactory cf;
		PublicKey publicKey = null;
		InputStream is = null;
		
		try
		{
			Resource resource = new UrlResource(certificatePath);
			
			if(!resource.exists())
			{
				throw new CoreException("Certificate not found");
			}
			
			is = resource.getInputStream();
		    cf = CertificateFactory.getInstance("X.509");
		    Certificate cert = cf.generateCertificate(is);
		    publicKey = cert.getPublicKey();
		}
		catch (Exception e)
		{
		    throw new CoreException("Failed to load public certificate", e);
		}
		finally
		{
			ClosureUtil.close(is);
		}

		Cipher publicChiper = null;
		try
		{
		    publicChiper = Cipher.getInstance("RSA");
		    publicChiper.init(Cipher.DECRYPT_MODE, publicKey);
		    byte[] decodedMessage = decoder.decodeBuffer(cipher);
		    byte[] decrypted = publicChiper.doFinal(decodedMessage);
		    
		    return new String(decrypted);
		}
		catch (Exception e)
		{
			throw new CoreException("Failed to decrypt cipher", e);
		}
	}
	
	public static String encryptWithPrivateKey(String privateKeyPath, String plainText) throws CoreException
	{		
		InputStream is = null;
		DataInputStream dis = null;
		KeyFactory keyFactory;
		PrivateKey privateKey = null;
		try
		{
			Resource resource = new UrlResource(privateKeyPath);
			
			if(!resource.exists())
			{
				throw new CoreException("Private key not found: "+privateKeyPath);
			}
			
			is = resource.getInputStream();
			dis = new DataInputStream(is);
		    byte[] privKeyBytes = new byte[(int) resource.contentLength()];
		    dis.readFully(privKeyBytes);
		    
		    keyFactory = KeyFactory.getInstance("RSA");
		    
		    PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
		    privateKey = (PrivateKey) keyFactory.generatePrivate(privSpec);
		}
		catch (Exception e)
		{
		    throw new CoreException("Failed to load private key", e);
		}
		finally
		{
			ClosureUtil.close(dis);
			ClosureUtil.close(is);
		}

		BASE64Encoder encoder = new BASE64Encoder();
		Cipher privateChiper = null;
		byte[] encrypted = null;
		String encryptedText = null;
		
		try
		{
			privateChiper = Cipher.getInstance("RSA");
		    privateChiper.init(Cipher.ENCRYPT_MODE, privateKey);
		    encrypted =  privateChiper.doFinal(plainText.getBytes());
		    encryptedText = encoder.encode(encrypted);
		    
		    return new String(encryptedText);
		}
		catch (Exception e)
		{
			throw new CoreException("Failed to encrypt plain text", e);
		}
	}
	
	public static String encodeDES(String secret, String plainText)
	{
		try
		{
			DESKeySpec keySpec = new DESKeySpec(secret.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			sun.misc.BASE64Encoder base64encoder = new BASE64Encoder();
	
			byte[] cleartext = plainText.getBytes("UTF8");      
	
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return base64encoder.encode(cipher.doFinal(cleartext));
		}
		catch(Exception e)
		{
			LOG.error("Error encoding text", e);
		}
		return null;
	}
	
	public static String decodeDES(String secret, String encrypted)
	{
		try
		{
			DESKeySpec keySpec = new DESKeySpec(secret.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			sun.misc.BASE64Decoder base64decoder = new BASE64Decoder();
	
			byte[] encrypedPwdBytes = base64decoder.decodeBuffer(encrypted);

			Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(encrypedPwdBytes));
		}
		catch(Exception e)
		{
			LOG.error("Error decoding text", e);
		}
		return null;
	}
}

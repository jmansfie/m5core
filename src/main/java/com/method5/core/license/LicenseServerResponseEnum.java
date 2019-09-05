package com.method5.core.license;

public enum LicenseServerResponseEnum
{
	VALID(1), INVALID(-1), ERROR(-2);

	private int code;
	
	private LicenseServerResponseEnum(int code)
	{
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}

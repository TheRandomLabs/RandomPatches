package com.therandomlabs.randomlib.config;

public class ConfigException extends RuntimeException {
	private static final long serialVersionUID = 7219817454671602843L;

	public ConfigException(String propertyName, Throwable cause) {
		super("Exception regarding property " + propertyName, cause);
	}
}

package com.abdullah4191.mename.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationException.class);

	public ApplicationException(String msg) {
		LOGGER.error(msg);
		System.out.println(msg);
	}

}

package com.gnb.web;

import com.gnb.exception.GenericException;


/**
 * class treating exceptions
 * 
 * @author 
 * 
 */
public class WebException extends GenericException {


	private static final long serialVersionUID = -1444999467698161412L;

	/**
	 * different codes to identify each exception
	 */
	public static final int CODE_EXCEPTION_NO_CONNECTION = 1;
	public static final int CODE_EXCEPTION_TIMEOUT = 2;
	public static final int CODE_EXCEPTION_URL_SYNTAX = 3;
	public static final int CODE_EXCEPTION_RECEIVING_DATA = 4;
	public static final int CODE_EXCEPTION_UNSUPPORTED_ENCODING = 5;
	public static final int CODE_EXCEPTION_WRONG_FORMAT_JSON = 6;
	
	protected WebException(int code, int shortMessage,
			int longMessage) {
		super(code,shortMessage, longMessage);
	}
	
	protected WebException(int code) {
		super(code);
	}

	/**
	 * 
	 * @param code
	 *            Exception code
	 * @param shortMessage
	 *            a short message as title of an alert
	 * @param longMessage
	 *            message describing the exception
	 * @return return an instance of WebException
	 */
	public static WebException getException(int code, int shortMessage,
			int longMessage) {
		return new WebException(code, shortMessage, longMessage);
	}

	public static WebException getException(int code) {
		return new WebException(code);
	}

	
}

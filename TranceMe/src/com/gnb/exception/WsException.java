package com.gnb.exception;

import com.gnb.web.WebException;


public class WsException extends WebException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int PARSE_ERROR = 10;
	public static final int IO_ERROR = 11;
	
	protected WsException(int code, int shortMessage,
			int longMessage) {
		super(code,shortMessage, longMessage);
	}
	
	protected WsException(int code) {
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
	 * @return return an instance of WsException
	 */
	public static WsException getException(int code, int shortMessage,
			int longMessage) {
		return new WsException(code, shortMessage, longMessage);
	}

	/**
	 * @param code
	 * @return return an instance of WsException
	 */
	public static WsException getException(int code) {
		return new WsException(code);
	}

}

package com.gnb.exception;


public abstract class GenericException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/**
	 * the code : identifier of exception
	 */
	private int code;
	/**
	 * a short message that will be displayed as a title of an alert
	 */
	private int shortMessage;
	/**
	 * message describing the exception
	 */
	private int longMessage;
	
	/**
	 * Constructor
	 * 
	 * @param code
	 *            Exception code
	 * @param shortMessage
	 *            short message as a title of an alert
	 * @param longMessage
	 *            message describing the exception
	 */
	protected GenericException(int code, int shortMessage, int longMessage) {
		super();
		this.code = code;
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	protected GenericException(int code) {
		super();
		this.code = code;
	}
	

	/**
	 * set the exception code
	 * 
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * get the Exception code
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * set the shortMessage
	 * 
	 * @param shortMessage
	 */
	public void setShortMessage(int shortMessage) {
		this.shortMessage = shortMessage;
	}

	/**
	 * get the sort message
	 * 
	 * @return
	 */
	public int getShortMessage() {
		return shortMessage;
	}

	/**
	 * set the message describing the exception
	 * 
	 * @param longMessage
	 */
	public void setLongMessage(int longMessage) {
		this.longMessage = longMessage;
	}

	/**
	 * get the message describing the exception
	 * 
	 * @return
	 */
	public int getLongMessage() {
		return longMessage;
	}
}

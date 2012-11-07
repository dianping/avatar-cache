/**
 * Project: avatar-cache
 * 
 * File Created at 2010-7-12
 * $Id$
 * 
 * Copyright 2010 Dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dianping.com.
 */
package com.dianping.cache.core;

/**
 * A runtime cache exception
 * wrap the concrete cache implement's original exception
 * @author danson.liu
 *
 */
public class CacheException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -270209742367889082L;
	
	/**
	 * Construct a <code>CacheException</code> with the specified detail message
	 * @param msg the detail message
	 */
	public CacheException(String msg) {
		super(msg);
	}
	
	/**
	 * Construct a <code>CacheException</code> with the specified detail message
	 * and nested exception.
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public CacheException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Retrieve the innermost cause of this exception, if any
	 * @return the innermost exception, or <code>null</code> if none
	 */
	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return rootCause;
	}
	
	/**
	 * Retrieve the most specific cause of this exception, that is,
	 * either the innermost cause (root cause) or this exception itself
	 * @return the most specific cause (never <code>null</code>)
	 */
	public Throwable getMostSpecificCause() {
		Throwable rootCause = getRootCause();
		return (rootCause != null ? rootCause : this);
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage();
		Throwable cause = super.getCause();
		if (cause != null) {
			StringBuffer buf = new StringBuffer();
			if (message != null) {
				buf.append(message).append("; ");
			}
			buf.append("nested exception is ").append(cause);
			return buf.toString();
		}
		else {
			return message;
		}
	}

}

package com.dianping.cache.queue;

/**
 * 
 * @author Leo Liang
 */
public class FileQueueClosedException extends Exception {

	private static final long	serialVersionUID	= 6780761235961823055L;

	public FileQueueClosedException() {
		super();
	}

	public FileQueueClosedException(String msg) {
		super(msg);
	}
}

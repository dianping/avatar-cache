package com.dianping.cache.queue;

import java.util.concurrent.TimeUnit;

/**
 * 文件队列
 * 
 * @author Leo Liang
 */
public interface FileQueue<T> {

	public T get();

	public T get(long timeout, TimeUnit timeUnit);

	public void add(T m) throws FileQueueClosedException;

	public void close();
}

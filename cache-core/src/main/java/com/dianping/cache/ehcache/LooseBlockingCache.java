/**
 * 
 */
package com.dianping.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;

/**
 * 在已有的cache api的情形下，对blockingcache进行的折中,BlockingCache本身要求如下编程模型(对并发的更精细的控制，避免重复的缓存重建)：
 * Element ele = null;
 * try {
 * 		ele = blockingCache.get(key);
 * } catch (RuntimeException e) {
 * 		blockingCache.put(new Element(key, null));
 * }
 * if (ele == null) {
 * 		Object item = null;
 * 		try {
 * 			item = retrieveAndBuildItemFromDataSource();
 * 		} catch (Exception e) {
 * 			blockingCache.releaseWriteLockOnKey(key);
 * 			throw e;
 * 		}
 * 		try {
 * 			blockingCache.put(new Element(key, item));
 * 		} finally {
 * 			return item;
 * 		}
 * }
 * return ele.getVal();
 * 
 * @author danson.liu
 *
 */
public class LooseBlockingCache extends BlockingCache {

	public LooseBlockingCache(Ehcache cache) throws CacheException {
		super(cache);
	}
	
	public LooseBlockingCache(final Ehcache cache, int numberOfStripes) {
		super(cache, numberOfStripes);
	}
	
	@Override
	public Element get(Object key) throws RuntimeException {
		Sync lock = getLockForKey(key);
        acquiredLockForKey(key, lock, LockType.READ);
        try {
            return underlyingCache.get(key);
        } finally {
            lock.unlock(LockType.READ);
        }
	}
	
    private void acquiredLockForKey(final Object key, final Sync lock, final LockType lockType) {
        if (timeoutMillis > 0) {
            try {
                boolean acquired = lock.tryLock(lockType, timeoutMillis);
                if (!acquired) {
                    StringBuilder message = new StringBuilder("Lock timeout. Waited more than ")
                            .append(timeoutMillis)
                            .append("ms to acquire lock for key ")
                            .append(key).append(" on blocking cache ").append(underlyingCache.getName());
                    throw new LockTimeoutException(message.toString());
                }
            } catch (InterruptedException e) {
                throw new LockTimeoutException("Got interrupted while trying to acquire lock for key " + key, e);
            }
        } else {
            lock.lock(lockType);
        }
    }

}

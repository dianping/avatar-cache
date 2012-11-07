package com.dianping.cache.kvdb;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.dianping.cache.memcached.KvdbTranscoder;
import com.dianping.cache.memcached.MemcachedClientConfiguration;
import com.dianping.cache.memcached.MemcachedClientImpl;

@Ignore
public class KvdbClientImplTest {
	
	private MemcachedClientImpl kvdbClient;
	
	@Before
	public void before() {
		kvdbClient = new MemcachedClientImpl();
		MemcachedClientConfiguration config = new MemcachedClientConfiguration();
		config.setServers(Arrays.asList("10.1.1.114:22211", "10.1.1.114:22211,10.1.1.115:22211,10.1.1.116:22211"));
		config.setTranscoder(new KvdbTranscoder());
		kvdbClient.setKey("demo-kvdb");
		kvdbClient.init(config);
		kvdbClient.start();
	}

	@Test
	public void test() throws InterruptedException {
		String key = "test-1";
		String value = "foo";
		kvdbClient.set(key, value, 0, null);
		Thread.sleep(1000);
		Object found = kvdbClient.get(key, null);
		Assert.assertNotNull(found);
		Assert.assertEquals(value, found);
	}
	
}

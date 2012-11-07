package com.dianping.avatar.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.avatar.cache.CacheKey;
import com.dianping.avatar.cache.CacheService;
import com.dianping.avatar.cache.annotation.Cache;

public class TestCat {

	static final String CATEGORY = "Adwords_Deal_API_Source";

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext content = new ClassPathXmlApplicationContext("AOPDemo2.xml");
		CacheService service = (CacheService) content.getBean("cacheService");

		String strKey = "Testkey1";
		service.add(strKey, "value");
		Thread.sleep(1000);
		System.out.println(service.get(strKey));
		service.remove("memcached", strKey);
		System.out.println(service.get(strKey));

		CacheKey key = new CacheKey(CATEGORY, "yong.you");
		service.add(key, "cacheValue");
		System.out.println(service.get(key));
		service.remove(key);
		System.out.println(service.get(key));

		// service.add(new User("test", "test"));

	}

	@Cache(category = CATEGORY, fields = { "name" })
	public static class User {
		private String name;

		private String age;

		public User(String name, String age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAge() {
			return age;
		}

		public void setAge(String age) {
			this.age = age;
		}
	}
}

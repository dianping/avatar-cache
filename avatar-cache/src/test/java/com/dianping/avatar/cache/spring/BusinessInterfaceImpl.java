package com.dianping.avatar.cache.spring;

public class BusinessInterfaceImpl implements BusinessInterface {
	
	public void hello() {
		System.out.println("hello Spring AOP.");
	}

	public int bye() {
		System.out.println("ByeByte AOP");
		return 200;
	}
	
	
}

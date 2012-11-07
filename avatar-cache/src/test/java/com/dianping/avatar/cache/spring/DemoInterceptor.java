package com.dianping.avatar.cache.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
public class DemoInterceptor implements MethodInterceptor, InitializingBean{  

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		System.out.println("Beginning method (1): "
				+ methodInvocation.getMethod().getDeclaringClass() + "."
				+ methodInvocation.getMethod().getName() + "()");
		long startTime = System.currentTimeMillis();
		try {
			Object result = methodInvocation.proceed();
			if(result != null) {
				System.out.println("returned: " + result);
			}
			return result;
		} finally {
			System.out.println("Ending method (1): "
					+ methodInvocation.getMethod().getDeclaringClass() + "."
					+ methodInvocation.getMethod().getName() + "()");
			System.out.println("Method invocation time (1): "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	}
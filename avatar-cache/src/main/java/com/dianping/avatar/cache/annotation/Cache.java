/**
 * Project: avatar
 * 
 * File Created at 2010-7-13
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
package com.dianping.avatar.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation for cache method and class. If class is annotated by {@link Cache}
 * ,it will be considered to cache instance,the cache parameters will be made
 * from {@link CacheParam} fields and {@link #fields()} declaring fields.
 * <p>
 * 
 * <p>
 * Generally, {@link CacheParam} will be convenient and clear, but if want to
 * take fields from super classes and that are not be annotated by
 * {@link CacheParam}, it is useful to declare them through {@link #fields()}.
 * </p>
 * 
 * <p>
 * If using {@link CacheParam} the fields order will be decided for
 * <tt>order</tt> property. If order
 * </p>
 * 
 * @author guoqing.chen
 * 
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

	/**
	 * cache category, correspond to 'name' attribute in cache configuration
	 */
	String category();

	/**
	 * Cache operation for method
	 */
	com.dianping.avatar.cache.annotation.CacheOperation operation() default com.dianping.avatar.cache.annotation.CacheOperation.SetAndGet;

	/**
	 * Default fields will be cached
	 */
	String[] fields() default "";
}

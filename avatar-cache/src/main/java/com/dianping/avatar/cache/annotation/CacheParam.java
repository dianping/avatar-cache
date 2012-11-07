/**
 * Project: avatar
 * 
 * File Created at 2010-7-20
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
 * Annotation for method parameter or field. If it annotated method parameter,
 * the <tt>order</tt> attribute will be ignored, the natural parameter order
 * will be adopted.
 * </p>
 * <p>
 * If annotated field,the order attribute will be available, and if two fields
 * have same order, the real order will be resolved by appearance order.
 * </p>
 * 
 * @author danson.liu
 * 
 */
@Target( { ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheParam {
	/**
	 * Parameter index
	 */
	int order() default 0;
}

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
package com.dianping.avatar.cache.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import com.dianping.avatar.cache.CacheKey;
import com.dianping.avatar.cache.annotation.Cache;
import com.dianping.avatar.cache.annotation.CacheParam;
import com.dianping.avatar.exception.SystemException;
import com.dianping.avatar.util.ClassUtils;

/**
 * Cache annotation parser helper utility
 * 
 * @author guoqing.chen
 * 
 */
public class CacheAnnotationUtils {

	/**
	 * Retrieve the cache key values from entity instance
	 */
	public static Object[] getCacheKeyValues(Object entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity is null.");
		}

		Class<?> cz = entity.getClass();

		Cache cache = cz.getAnnotation(Cache.class);

		if (cache == null) {
			throw new SystemException("The entity must be annotated by Cache.");
		}

		Field[] fields = ClassUtils.getDeclaredFields(cz);

		final List<OrderedField> cacheFields = new ArrayList<OrderedField>();

		// Extract annotated fields
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			CacheParam fCache = f.getAnnotation(CacheParam.class);
			if (fCache != null) {
				cacheFields.add(new OrderedField(f, i, fCache.order()));
			}
		}

		// Extract declared fields
		for (int i = 0; i < cache.fields().length; i++) {
			String fieldName = cache.fields()[i];
			if (fieldName.isEmpty()) {
				continue;
			}
			Field f = ReflectionUtils.findField(cz, fieldName);
			if (f == null) {
				throw new IllegalArgumentException("Invalid cahce parameter " + fieldName
						+ ", the filed is not exists.");
			}

			cacheFields.add(new OrderedField(f, i, -Integer.MAX_VALUE + i));
		}

		Collections.sort(cacheFields);

		Object[] values = new Object[cacheFields.size()];

		for (int i = 0; i < cacheFields.size(); i++) {
			OrderedField oField = cacheFields.get(i);

			ReflectionUtils.makeAccessible((Field) oField.field);

			values[i] = ReflectionUtils.getField((Field) oField.field, entity);
		}

		return values;
	}

	/**
	 * Generate {@link CacheKey} instance by {@link Method} and arguments.The
	 * method should be annotated by {@link Cache}
	 */
	public static CacheKey getCacheKey(final Method method, final Object[] args) {

		if (method == null || args == null) {
			throw new IllegalArgumentException("method/argus must not be null.");
		}

		Cache cache = method.getAnnotation(Cache.class);

		if (cache == null) {
			return null;
		}

		Annotation[][] annos = method.getParameterAnnotations();

		final List<OrderedField> orderedParams = new ArrayList<OrderedField>();

		for (int i = 0; i < annos.length; i++) {
			Annotation[] paramAnnos = annos[i];
			for (int j = 0; j < paramAnnos.length; j++) {
				Annotation paramAnno = paramAnnos[j];
				if (paramAnno instanceof CacheParam) {
					CacheParam cacheParam = (CacheParam) paramAnno;
					orderedParams.add(new OrderedField(args[i], i, cacheParam.order()));
					break;
				}
			}
		}

		Collections.sort(orderedParams);

		Object[] values = new Object[orderedParams.size()];

		for (int i = 0; i < orderedParams.size(); i++) {
			OrderedField oField = orderedParams.get(i);

			values[i] = oField.field;
		}

		return new CacheKey(cache.category(), values);
	}

	/**
	 * Retrieve cache category for class <tt>cz</tt> that have to be annotated
	 * by {@link Cache}
	 */
	public static String getCacheCategory(Class<?> cz) {
		if (cz == null) {
			throw new IllegalArgumentException("Parameter cz is null.");
		}

		Cache cache = cz.getAnnotation(Cache.class);

		if (cache == null) {
			throw new SystemException("The cz must be annotated by Cache.");
		}

		return cache.category();
	}

	/**
	 * Ordered field object
	 */
	static class OrderedField implements Comparable<OrderedField> {
		/**
		 * Field
		 */
		public final Object field;
		/**
		 * Field index
		 */
		public final int index;
		/**
		 * Field order
		 */
		public final int order;

		/**
		 * Constructor
		 */
		public OrderedField(Object field, int index, int order) {
			this.field = field;
			this.index = index;
			this.order = order;
		}

		@Override
		public int compareTo(OrderedField target) {

			if (target == this) {
				return 0;
			}

			int ret = this.order - target.order;

			if (ret == 0) {
				ret = this.index - target.index;
			}

			return ret;
		}
	}
}

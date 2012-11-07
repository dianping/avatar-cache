/**
 * Project: cache-core
 * 
 * File Created at 2010-8-24
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
package com.dianping.cache.core;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.caucho.hessian.io.Hessian2Output;

/**
 * Test hessian serialize
 * 
 * @author guoqing.chen
 * 
 */
public class TestHessian {

	@Test
	public void testHessian() throws Exception {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Hessian2Output output = new Hessian2Output(bout);

		long t1 = System.currentTimeMillis();

		for (int i = 0; i < 100000; i++) {
			User user = new User(100, "dianping.com" + i);
			user.mobiles.add("mobile" + i);
			user.mobiles.add("mobile2" + (i + 1));
			output.writeObject(user);
		}

		long interval1 = System.currentTimeMillis() - t1;

		ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
		ObjectOutputStream oOutput = new ObjectOutputStream(bout2);

		long t2 = System.currentTimeMillis();

		for (int i = 0; i < 100000; i++) {
			User user = new User(100, "dianping.com" + i);
			user.mobiles.add("mobile" + i);
			user.mobiles.add("mobile2" + (i + 1));

			oOutput.writeObject(user);
		}

		long interval2 = System.currentTimeMillis() - t2;

		System.out.printf("Hessian (%d,%d), RMI:(%d,%d)", bout.size(), interval1, bout2.size(), interval2);
	}

	public static class User implements Serializable {
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = -4754574594493166572L;

		public final int id;
		public final String name;

		public final List<String> mobiles = new ArrayList<String>();

		/**
		 * @param id
		 * @param name
		 */
		public User(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
	}
}

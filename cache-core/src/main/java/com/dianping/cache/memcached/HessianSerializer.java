/**
 * Project: cache-core
 * 
 * File Created at 2010-8-23
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
package com.dianping.cache.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.spy.memcached.compat.SpyObject;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * HessianSerializer for serialize and deserialize serializable object
 * @author danson.liu
 *
 */
public class HessianSerializer extends SpyObject {

	/**
	 * @param o
	 * @return
	 */
	public byte[] serialize(Object o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Hessian2Output hessian2Output = new Hessian2Output(baos);
			hessian2Output.writeObject(o);
			//place here not finally block, because byte array stream is not required to be closed
			hessian2Output.close();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		}
	}

	/**
	 * @param data
	 * @return
	 */
	public Object deserialize(byte[] data) {
		Object rv=null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			Hessian2Input hessian2Input = new Hessian2Input(bais);
			rv = hessian2Input.readObject();
			//place here not finally block, because byte array stream is not required to be closed
			hessian2Input.close();
		} catch(IOException e) {
			getLogger().warn("Caught IOException decoding %d bytes of data", data.length, e);
		}
		return rv;
	}

}

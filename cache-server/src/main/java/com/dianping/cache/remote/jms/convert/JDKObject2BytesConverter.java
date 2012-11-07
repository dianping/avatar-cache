/**
 * Project: com.dianping.cache-server-2.0.1
 * 
 * File Created at 2011-9-23
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.cache.remote.jms.convert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * JDKObject2BytesConverter
 * @author youngphy.yang
 *
 */
public class JDKObject2BytesConverter implements Object2BytesConverter{

	/**
	 * parameter object should be serializable
	 * @throws IOException 
	 */
	@Override
	public byte[] convertObject2Bytes(Object object) throws IOException {
		byte[] result = null;
		if(object != null) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = null;
			try {
				oo = new ObjectOutputStream(bo);
				oo.writeObject(object);
				result = bo.toByteArray();
			} finally {
				if (oo != null) {
					oo.close();
				}
			}
		}
		return result;
	}

}

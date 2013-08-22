/**
 * Project: com.dianping.cache-core-2.0.0
 * 
 * File Created at 2011-4-19
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
package com.dianping.cache.core.ketama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TODO Comment of KetamaHashTest
 * @author jian.liu
 *
 */
public class KetamaHashAlgorithmTest {

//	public static void main(String[] args) {
//		MockKetamaNodeLocator ketamaNodeLocator = new MockKetamaNodeLocator(Arrays.asList("192.168.8.46:11211", "192.168.8.47:11211", "192.168.8.45:11211", "192.168.8.48:11211"));
//		Map<String, List<String>> stats = new HashMap<String, List<String>>();
//		for (int i = 0; i < 100; i++) {
//			String key = "cache-key-" + i;
//			String primary = ketamaNodeLocator.getPrimary(key);
////			System.out.println(key + "=" + primary);
//			if (!stats.containsKey(primary)) {
//				stats.put(primary, new ArrayList<String>());
//			}
//			List<String> list = stats.get(primary);
//			list.add(key);
//		}
//		for (Entry<String, List<String>> entry : stats.entrySet()) {
//			List<String> nodeList = entry.getValue();
////			String nodeString = "";
////			for (String node : nodeList) {
////				nodeString += node + ", ";
////			}
//			System.out.println("------------" + entry.getKey() + "[" + nodeList.size() + "]" + "------------");
//			for (String node : nodeList) {
//				System.out.println(node);
//			}
//		}
//	}
	
}

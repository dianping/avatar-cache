package com.dianping.cache.core.ketama;

import java.util.HashMap;
import java.util.Map;


public class MockKetamaNodeLocatorConfiguration {
	
	protected Map<String, String> socketAddresses = new HashMap<String, String>();

	public String getKeyForNode(String node, int repetition) {
		return getSocketAddressForNode(node) + "-" + repetition;
	}

	private String getSocketAddressForNode(String node) {
		String result=socketAddresses.get(node);
		if(result == null) {
			result = node;
			if (result.startsWith("/")) {
				result = result.substring(1);
			}
			socketAddresses.put(node, result);
		}
		return result;
	}

	public int getNodeRepetitions() {
		return 160;
	}

}

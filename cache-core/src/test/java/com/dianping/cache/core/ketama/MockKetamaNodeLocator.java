package com.dianping.cache.core.ketama;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import net.spy.memcached.HashAlgorithm;

public class MockKetamaNodeLocator {
//	
//	final Collection<String> allNodes;
//	final SortedMap<Long, String> ketamaNodes;
//	final MockKetamaNodeLocatorConfiguration config;
//	
//	public MockKetamaNodeLocator(List<String> nodes) {
////		allNodes = nodes;
////		ketamaNodes=new TreeMap<Long, String>();
////		config = new MockKetamaNodeLocatorConfiguration();
////		
////		int numReps= config.getNodeRepetitions();
////		for(String node : nodes) {
////			// Ketama does some special work with md5 where it reuses chunks.
////			for(int i=0; i<numReps / 4; i++) {
////			    byte[] digest=HashAlgorithm.computeMd5(config.getKeyForNode(node, i));
////				for(int h=0;h<4;h++) {
////					Long k = ((long)(digest[3+h*4]&0xFF) << 24)
////						| ((long)(digest[2+h*4]&0xFF) << 16)
////						| ((long)(digest[1+h*4]&0xFF) << 8)
////						| (digest[h*4]&0xFF);
////					ketamaNodes.put(k, node);
////				}
////
////			}
////		}
////		assert ketamaNodes.size() == numReps * nodes.size();
//	}
//	
//	public String getPrimary(final String k) {
//	    String rv=getNodeForKey(HashAlgorithm.KETAMA_HASH.hash(k));
//		assert rv != null : "Found no node for key " + k;
//		return rv;
//	}
//	
//	String getNodeForKey(long hash) {
//		final String rv;
//		if(!ketamaNodes.containsKey(hash)) {
//			// Java 1.6 adds a ceilingKey method, but I'm still stuck in 1.5
//			// in a lot of places, so I'm doing this myself.
//			SortedMap<Long, String> tailMap = ketamaNodes.tailMap(hash);
//			if(tailMap.isEmpty()) {
//				hash=ketamaNodes.firstKey();
//			} else {
//				hash=tailMap.firstKey();
//			}
//		}
//		rv= ketamaNodes.get(hash);
//		return rv;
//	}

}

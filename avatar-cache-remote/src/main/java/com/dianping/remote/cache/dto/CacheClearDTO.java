/**
 * 
 */
package com.dianping.remote.cache.dto;

import java.util.List;

import com.dianping.remote.share.dto.AbstractDTO;

/**
 * @author jian.liu
 *
 */
public class CacheClearDTO extends AbstractDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8063849999710998236L;
	
	private String cacheType;
	
	private String key;
	
	private String category;
	
	private List<Object> params;

	public CacheClearDTO(String cacheType, String key, String category, List<Object> params) {
		this.cacheType = cacheType;
		this.key = key;
		this.category = category;
		this.params = params;
	}
	
	public CacheClearDTO() {
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

}

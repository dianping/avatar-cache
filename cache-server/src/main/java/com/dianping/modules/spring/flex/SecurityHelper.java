package com.dianping.modules.spring.flex;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Created by IntelliJ IDEA.
 * User: danson
 * Date: 2010-3-10
 * Time: 23:17:44
 * To change this template use File | Settings | File Templates.
 */
public class SecurityHelper {
	
	public static final String EMBED_PRINCIPAL_SUFFIX = "@!@";

    public static Map<String, Object> getAuthentication() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Map<String, Object> authenticationResult = new HashMap<String, Object>();
        authenticationResult.put("name", authentication.getName());
        String[] authorities = new String[authentication.getAuthorities().length];
        for (int i = 0; i < authorities.length; i++) {
            authorities[i] = authentication.getAuthorities()[i].getAuthority();
        }
        authenticationResult.put("authorities", authorities);
        authenticationResult.put("details", authentication.getDetails());
        return authenticationResult;
    }
    
    @SuppressWarnings("unchecked")
	public static String getPrincipal() {
    	Map<String, Object> authentication = getAuthentication();
    	if (authentication == null) {
    		return null;
    	}
    	
    	Object detailsObj = authentication.get("details");
    	
    	if(!(detailsObj instanceof Map)) {
    	    return null;
    	}
    	
    	Map<String, Object> details = (Map<String, Object>) detailsObj;
    	String realName = null;
    	if (details != null) {
    		realName = (String) details.get("realName");
    	}
    	return (String) authentication.get("name") + (realName != null ? "(" + realName + ")" : "");
    }
    
    public static boolean isEmbededPrincipal(String principal) {
    	return principal != null && principal.endsWith(EMBED_PRINCIPAL_SUFFIX);
    }
    
    public static String getPrincipalWithEmbeded(String principal) {
    	if (isEmbededPrincipal(principal)) {
    		return principal.substring(0, principal.length() - EMBED_PRINCIPAL_SUFFIX.length());
    	}
    	return principal;
    }

}

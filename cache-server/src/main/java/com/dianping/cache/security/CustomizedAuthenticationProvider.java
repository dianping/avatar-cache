/**
 * Project: cache-server
 * 
 * File Created at 2011-9-21
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
package com.dianping.cache.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.DaoAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;

import com.dianping.cache.entity.Resource;
import com.dianping.cache.entity.Role;
import com.dianping.cache.entity.User;
import com.dianping.cache.service.UserService;
import com.dianping.modules.spring.flex.SecurityHelper;

/**
 * @author danson.liu
 *
 */
public class CustomizedAuthenticationProvider extends DaoAuthenticationProvider {
	
	private UserService userService;

	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
		UsernamePasswordAuthenticationToken successAuth = (UsernamePasswordAuthenticationToken) super.createSuccessAuthentication(principal, authentication, user);
		User userEntity = userService.findUser(user.getUsername());
		Map<String, Object> details = new HashMap<String, Object>();
		if (userEntity != null) {
			details.put("realName", userEntity.getRealName());
			enrichAuthDetails(userEntity, details);
			String username = authentication.getName();
			if (SecurityHelper.isEmbededPrincipal(username)) {
				details.put("embed-principal", username);
			}
		}
		successAuth.setDetails(details);
		return successAuth;
	}
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
		throws AuthenticationException {
		String username = authentication.getName();
		if (!SecurityHelper.isEmbededPrincipal(username)) {
			super.additionalAuthenticationChecks(userDetails, authentication);
		}
	}

	private void enrichAuthDetails(User userEntity, Map<String, Object> details) {
		List<Role> roleList = userEntity.getRoleList();
		Set<String> authDetails = new HashSet<String>();
		if (roleList != null) {
			for (Role role : roleList) {
				for (Resource resource : role.getResourceList()) {
					authDetails.add(resource.getName());
				}
			}
		}
		details.put("authDetails", authDetails);
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}

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

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import com.dianping.cache.entity.Role;
import com.dianping.cache.service.UserService;
import com.dianping.modules.spring.flex.SecurityHelper;

/**
 * @author danson.liu
 *
 */
public class CustomizedUserDetailsService implements UserDetailsService {
	
	public static final String ROLE_CHANNEL_ACCESSOR = "ROLE_USER";
	private static final String ROLE_SUPER = "SUPER_CACHE_ADMIN";
	
	private UserService userService;
	
	private String superName;
	private String superPass;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		username = SecurityHelper.getPrincipalWithEmbeded(username);
		if (superName.equals(username)) {
			return new User(superName, superPass, true, true, true, true, new GrantedAuthority[] {
				new GrantedAuthorityImpl(ROLE_SUPER), new GrantedAuthorityImpl(ROLE_CHANNEL_ACCESSOR) 
			});
		} else {
			com.dianping.cache.entity.User user = userService.findUser(username);
			if (user != null) {
				List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
				if (user.getRoleList() != null) {
					for (Role role : user.getRoleList()) {
						authList.add(new GrantedAuthorityImpl(role.getName()));
					}
				}
				authList.add(new GrantedAuthorityImpl(ROLE_CHANNEL_ACCESSOR));
				GrantedAuthority[] authorities = authList.toArray(new GrantedAuthority[authList.size()]);
				return new User(user.getName(), user.getPassword().toLowerCase(), true, true, true, true, authorities);
			}
			throw new UsernameNotFoundException("User[" + username + "] not exists.");
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setSuperName(String superName) {
		this.superName = superName;
	}

	public void setSuperPass(String superPass) {
		this.superPass = superPass;
	}

}

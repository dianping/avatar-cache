/**
 * Project: cache-builder
 * 
 * File Created at 2010-9-3
 * $Id$
 * 
 * Copyright 2010 dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.cache.builder.metadata;

import org.w3c.dom.Element;

import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.ehcache.EhcacheConfiguration;

/**
 * EhcacheMetadataParser for Ehcache local cache
 * 
 * @author pengshan.zhang
 * 
 */
public class EhcacheMetadataParser implements CacheMetadataParser {

    private static final String XML_FILE = "xmlFile";

    @Override
    public CacheClientConfiguration parse(Element e) {
        EhcacheConfiguration configuration = new EhcacheConfiguration();
        String xmlFile = e.getAttribute(XML_FILE);
        if (xmlFile != null && xmlFile.length() > 0) {
            configuration.setXmlFile(xmlFile);
        }
        return configuration;
    }

}
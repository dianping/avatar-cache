/**
 * Project: cache-server
 * 
 * File Created at 2011-9-18
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
package com.dianping.cache.service.condition;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * @author danson.liu
 *
 */
public class OperationLogSearchCondition {

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	private static FastDateFormat DEFAULT_DAY_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");

	private String operator;
	
	private Date operateStart;
	
	private Date operateEnd;
	
	private String content;
	
	private boolean critical;
	
	private int succeed = -1;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getOperateStart() {
		return operateStart;
	}

	public void setOperateStart(Date operateStart) {
		this.operateStart = operateStart;
	}
	
	public void setOperateStartAsStr(String operateStart) throws ParseException {
		this.operateStart = DateUtils.parseDate(operateStart + " 00:00:00", new String[] {DATE_PATTERN});
	}
	
	public String getOperateStartAsStr() {
		return DEFAULT_DAY_FORMATTER.format(this.operateStart);
	}

	public Date getOperateEnd() {
		return operateEnd;
	}

	public void setOperateEnd(Date operateEnd) {
		this.operateEnd = operateEnd;
	}
	
	public void setOperateEndAsStr(String operateEnd) throws ParseException {
		this.operateEnd = DateUtils.parseDate(operateEnd + " 23:59:59", new String[] {DATE_PATTERN});
	}
	
	public String getOperateEndAsStr() {
		return DEFAULT_DAY_FORMATTER.format(this.operateEnd);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isCritical() {
		return critical;
	}

	public void setCritical(boolean critical) {
		this.critical = critical;
	}

	public int getSucceed() {
		return succeed;
	}

	public void setSucceed(int succeed) {
		this.succeed = succeed;
	}
	
}

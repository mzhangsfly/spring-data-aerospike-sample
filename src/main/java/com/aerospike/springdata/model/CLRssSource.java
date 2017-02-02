/**
 * Copyright 2012-2017 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.springdata.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * @author Michael Zhang
 *
 */
public class CLRssSource {
	
	private @Id String url;
	
	private Integer loadInterval;
	
	private Date expireDate;
	
	private Boolean enabled;
	
	private String email;
	
	public CLRssSource(){}
	
	/**
	 * @param url
	 */
	public CLRssSource(String url) {
		this(url, 10, null, null);
	}
	
	
	/**
	 * @param url
	 * @param loadIntervalMins
	 * @param expireDate
	 * @param email
	 */
	public CLRssSource(String url, Integer loadIntervalMins, Date expireDate, String email) {
		this(url, loadIntervalMins, expireDate, Boolean.TRUE, email);
	}


	/**
	 * @param url
	 * @param loadIntervalMins
	 * @param expireDate
	 * @param enabled
	 * @param email
	 */
	public CLRssSource(String url, Integer loadIntervalMins, Date expireDate, Boolean enabled, String email) {
		super();
		this.url = url;
		this.loadInterval = loadIntervalMins;
		this.expireDate = expireDate;
		this.enabled = enabled;
		this.email = email;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the loadIntervalMins
	 */
	public Integer getLoadInterval() {
		return loadInterval;
	}


	/**
	 * @param loadIntervalMins the loadIntervalMins to set
	 */
	public void setLoadInterval(Integer loadIntervalMins) {
		this.loadInterval = loadIntervalMins;
	}


	/**
	 * @return the expireDate
	 */
	public Date getExpireDate() {
		return expireDate;
	}


	/**
	 * @param expireDate the expireDate to set
	 */
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}


	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}


	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}

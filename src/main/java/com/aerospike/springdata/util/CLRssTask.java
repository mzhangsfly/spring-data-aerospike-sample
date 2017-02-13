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
package com.aerospike.springdata.util;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aerospike.springdata.model.CLRssSource;
import com.aerospike.springdata.service.AerospikeDataService;

/**
 * @author Michael Zhang
 *
 */
@Component
public class CLRssTask {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AerospikeDataService aerospikeDataService;

	private Map<String, ScheduledFuture<?>> activeTasks = new HashMap<String, ScheduledFuture<?>>();
	
	private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(4);

	@PostConstruct
	public void start() {
		log.debug("Starting RSS Data Load Services");
		for(CLRssSource src : aerospikeDataService.getRssSourceMap().values()){
			try {
				if(src.getEnabled())
					activeTasks.put(src.getUrl(), executor.scheduleAtFixedRate(new CLRssData(src.getUrl(), aerospikeDataService), 1, src.getLoadInterval(), TimeUnit.MINUTES));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addRssSourceTask(CLRssSource rss) throws MalformedURLException{
		if(!activeTasks.containsKey(rss.getUrl()))
			activeTasks.put(rss.getUrl(), executor.scheduleAtFixedRate(new CLRssData(rss.getUrl(), aerospikeDataService), 1, rss.getLoadInterval(), TimeUnit.MINUTES));
	}
	
	public void removeRssSourceTask(CLRssSource rss){
		activeTasks.remove(rss.getUrl()).cancel(false);
	}

}

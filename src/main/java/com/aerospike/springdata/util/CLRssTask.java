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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aerospike.springdata.model.CLRssSource;
import com.aerospike.springdata.service.AerospikeDataService;

/**
 * @author Michael Zhang
 *
 */
@Component
public class CLRssTask implements Runnable {
	
	@Autowired
	private AerospikeDataService aerospikeDataService;

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(4);
		
		for(CLRssSource src : aerospikeDataService.getRssSourceMap().values()){
			
			try {
				executor.scheduleAtFixedRate(new CLRssData(src.getUrl(), aerospikeDataService), 1, src.getLoadInterval(), TimeUnit.MINUTES);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

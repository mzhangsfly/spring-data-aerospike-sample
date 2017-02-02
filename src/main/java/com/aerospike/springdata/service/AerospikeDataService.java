/**
 * Copyright 2012-2016 Aerospike, Inc.
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

package com.aerospike.springdata.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.stereotype.Service;

import com.aerospike.client.query.IndexType;
import com.aerospike.springdata.model.CLRentPage;
import com.aerospike.springdata.model.CLRssSource;
import com.aerospike.springdata.repository.CLRentPageRepository;
import com.aerospike.springdata.repository.CLRssSourceRepository;
import com.aerospike.springdata.util.CLRssData;
import com.aerospike.springdata.util.CLRssTask;

/**
 * This class represents a User Defined Function
 * registered with a cluster
 *
 * @author Michael Zhang
 */

@Service
public class AerospikeDataService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AerospikeTemplate aerospikeTemplate;
	
	Map<String, CLRssSource> rssSourceMap = new ConcurrentHashMap<String, CLRssSource>();
	
	@Autowired
	protected CLRssSourceRepository rssRepository;

	@Autowired
	private CLRentPageRepository pageRepository;
	
	@Autowired
	private CLRssTask clRssTask;

	@PostConstruct
	public void createIndex(){
		log.debug("Running post Construct......");
		aerospikeTemplate.createIndex(CLRentPage.class, "Rent_Area_index", "area", IndexType.NUMERIC);
		aerospikeTemplate.createIndex(CLRentPage.class, "Rent_geoLoation_index", "geoLocation", IndexType.GEO2DSPHERE);
		aerospikeTemplate.createIndex(CLRentPage.class, "Rent_price_index", "price", IndexType.NUMERIC);
		aerospikeTemplate.createIndex(CLRentPage.class, "Rent_post_date_index", "postDate", IndexType.NUMERIC);
		aerospikeTemplate.createIndex(CLRssSource.class, "Rss_expiration_date_index", "expireDate", IndexType.NUMERIC);
		
		syncRssSource();
		rssSourceMap.put(CLRssData.CLSF_RENT_RSS_URL, new CLRssSource(CLRssData.CLSF_RENT_RSS_URL));
		new Thread(clRssTask).start();

	}

	/**
	 * @return the rssSourceMap
	 */
	public Map<String, CLRssSource> getRssSourceMap() {
		return rssSourceMap;
	}

	/**
	 * @param rssSourceMap the rssSourceMap to set
	 */
	public void setRssSourceMap(Map<String, CLRssSource> rssSourceMap) {
		this.rssSourceMap = rssSourceMap;
	}
	
	/***
	 * Query by geo radius and time from now
	 */
	public List<CLRentPage> fetchPage(double longitude, double latitude, double radius, long since){
		return pageRepository.findByGeoLocationWithinAndPostDateAfter(longitude, latitude, radius, since);
	}
	
	
	public void syncRssSource(){
		for(CLRssSource rr : rssRepository.findAll()){
			log.info("Adding RSS resouce: " + rr.getUrl());
			System.out.println(rr.getExpireDate());
			rssSourceMap.put(rr.getUrl(), rr);
		}
	}
	
	public void remove(String rssUrl){
		rssRepository.delete(rssUrl);
	}
	
	public void save(List<CLRentPage> pages){
		for(CLRentPage page : pages){
			System.out.println("saving: " + page.getId() + ", price = "+ page.getPrice());
			try{
				aerospikeTemplate.save(page.getId(), page);
			}catch(Exception e){e.printStackTrace();}
		}
//			pageRepository.save(pages);
	}
	
	public CLRssSource saveRssConfig(CLRssSource rss){
		return rssRepository.save(rss);
	}
}

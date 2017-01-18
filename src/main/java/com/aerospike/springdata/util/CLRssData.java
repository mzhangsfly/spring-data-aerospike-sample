/**
 *
 * Copyright 2012-2017 Aerospike, Inc.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aerospike.springdata.model.CLRentPage;
import com.aerospike.springdata.service.AerospikeDataService;

/**
 * Utility to parse CL rss feeding 
 * 
 * @author Michael Zhang
 *
 */
public class CLRssData implements Runnable{
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static String UTF_8 = "UTF-8";
	public static String CLSF_RENT_RSS_URL = "https://sfbay.craigslist.org/search/apa?format=rss";
	
	private String syncUpdateBase;
	private String rssUrl;

	protected AerospikeDataService dataService;

	public CLRssData(){}
	
	public CLRssData(String url, AerospikeDataService ds) throws MalformedURLException{
		rssUrl = url;
		dataService = ds;
	}
	
	public List<URL> loadRss() throws ParserConfigurationException, SAXException, IOException{
		return loadRss(new URL(rssUrl));
	}
	
	public List<URL> loadRss(URL url) throws ParserConfigurationException, SAXException, IOException{
		List<URL> urls = new LinkedList<URL>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		Reader reader = new InputStreamReader(url.openStream(),UTF_8);
		InputSource is = new InputSource(reader);
		is.setEncoding(UTF_8);
		Document doc = dbFactory.newDocumentBuilder().parse(is);
		
		doc.getDocumentElement().normalize();
		NodeList ll = doc.getElementsByTagName("syn:updateBase").item(0).getChildNodes();
		Node lastSync = ll.item(0);
		log.info ("update base: " + lastSync.getNodeValue());
		if(null != syncUpdateBase && syncUpdateBase.equals(lastSync.getNodeValue())){
			return urls;
		}else{
			syncUpdateBase = lastSync.getNodeValue();
		}
		
		NodeList nList = doc.getElementsByTagName("rdf:li");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				urls.add(new URL(eElement.getAttribute("rdf:resource")));
			}
		}
		return urls;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		log.info("Loading data from rss feed....");
		ExecutorService pool = Executors.newFixedThreadPool(3);
		try {
			List<CLRentPage> pages = new LinkedList<CLRentPage>();
			Set<Future<CLRentPage>> set = new HashSet<Future<CLRentPage>>();
			for (URL url : loadRss()){
				Callable<CLRentPage> pageParser = new CLRentPageParser(url);
				set.add(pool.submit(pageParser));
			}
			
			for (Future<CLRentPage> future : set) {
			      try {
					pages.add(future.get());
				} catch (ExecutionException e) {log.info("bad rental ads");}
			}
			
			for(CLRentPage p : pages){
				System.out.println(p);
			}
			
			dataService.save(pages);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} catch (InterruptedException | NullPointerException e) {
			e.printStackTrace();
		} 
		pool.shutdown();
		try {
			pool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

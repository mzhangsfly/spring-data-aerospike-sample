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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aerospike.client.Value;
import com.aerospike.client.Value.GeoJSONValue;
import com.aerospike.springdata.model.CLRentPage;
import com.joestelmach.natty.Parser;

/**
 * @author Michael Zhang
 *
 */
public class CLRentPageParser implements Callable<CLRentPage>{
	private static final String BEDROOM = "BR";
	private static final String BATH = "BA";
	private static final String FT = "FT";

	private Document doc;
	private URL url;

	public CLRentPageParser(URL url) throws IOException{
		this.url= url;
		init(url);
	}
	
	private void init(URL url) throws IOException{
		doc = Jsoup.parse(url, 10*1000);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public CLRentPage call() throws Exception {
		CLRentPage rentPage = null;
		Elements scripts = doc.getElementsByTag("script");
		for(Element script: scripts){
            Pattern p = Pattern.compile("pID = \"(.+?)\""); // Regex for the value of the key
            Matcher m = p.matcher(script.html()); // you have to use html here and NOT text! Text will drop the 'key' part
            while( m.find()){
            	rentPage = new CLRentPage(m.group(1)); // value only
            	rentPage.setUrl(this.url.toString());
            }
		}
		
		//get images
		try{
			for(Element thumb : doc.select("div#thumbs").first().select("a.thumb")){
				rentPage.getImages().add(thumb.attr("href"));
			}
		}catch(Exception e){}
		
		rentPage.setPostingBody(doc.select("section#postingbody").toString());
		NumberFormat format = NumberFormat.getInstance();
		try {
			rentPage.setPrice(format.parse(doc.select("span.price").first().ownText().replaceAll("[^\\d.]+", "")).floatValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element attrDiv = doc.select("div.mapAndAttrs").first();
		Element mapbox = attrDiv.select("div.mapbox").first().select("div.viewposting").first();
		String geoLoc = String.format("{ \"type\": \"Point\", \"coordinates\": [%f, %f] }", Float.parseFloat(mapbox.attr("data-longitude")), Float.parseFloat(mapbox.attr("data-latitude")));
		rentPage.setGeoLocation((GeoJSONValue) Value.getAsGeoJSON(geoLoc));
		
		Elements mapattr = attrDiv.select("p.attrgroup");
		for(Element ap : mapattr){
			for(Element espan : ap.getElementsByTag("span")){
				if(espan.ownText().equalsIgnoreCase(FT)){
					rentPage.setArea(Integer.parseInt(espan.getElementsByTag("b").first().ownText()));
				}else{
					for(Element ep : espan.getElementsByTag("b")){
						if(ep.ownText().toUpperCase().endsWith(BATH)){
							rentPage.setBath(Float.parseFloat(ep.ownText().toUpperCase().replace(BATH, "")));
						}
						if(ep.ownText().toUpperCase().endsWith(BEDROOM))rentPage.setBedroom(Float.parseFloat(ep.ownText().toUpperCase().replaceAll(BEDROOM, "")));
					}
				}
			}
		}
		Element postTime = doc.select("time.timeAgo").first();
		rentPage.setPostDate(new Parser().parse(postTime.attr("datetime")).get(0).getDates().get(0).getTime());
		String replyuri =  doc.select("span.replylink").first().select("a").first().attr("href");
		URL replyUrl = new URL(url.getProtocol(), url.getHost(), replyuri);
		Document reply = Jsoup.parse(replyUrl, 10*1000);
		try{
			rentPage.setPhone(reply.select("a.mailapp").first().ownText());
			rentPage.setEmail(reply.select("a.reply-tel-link").first().attr("href"));
		}catch(Exception e){}
		return rentPage;
	}
	
	static CLRentPage parsePosting(String posting) throws MalformedURLException, IOException, Exception{
		Document d = Jsoup.parse(posting);
		Element e = d.getElementsByTag("section").first();
		e = e.select("div.print-qrcode").first();
		String pageURL = e.attr("data-location");
		return new CLRentPageParser(new URL(pageURL)).call();
	}
	
	static String parseURLFromPosting(String posting) throws MalformedURLException, IOException, Exception{
		Document d = Jsoup.parse(posting);
		Element e = d.getElementsByTag("section").first();
		e = e.select("div.print-qrcode").first();
		return e.attr("data-location");
	}
	
}

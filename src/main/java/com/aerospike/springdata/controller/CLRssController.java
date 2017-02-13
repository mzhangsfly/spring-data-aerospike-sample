
package com.aerospike.springdata.controller;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aerospike.springdata.model.CLRentPage;
import com.aerospike.springdata.model.CLRssSource;
import com.aerospike.springdata.service.AerospikeDataService;

@RestController
public class CLRssController {
	private static int MAX_HOUR = 5 * 24;

	@Autowired
	AerospikeDataService aerospikeDataService;

	@RequestMapping("/user")
	public Principal user(Principal principal) {
		return principal;
	}

	@RequestMapping(value="/rss", method={RequestMethod.GET})
	public Collection<CLRssSource> index() {
		return aerospikeDataService.getRssSourceMap().values();
	}
	
	@RequestMapping(value="/rss", method={RequestMethod.POST})
	public CLRssSource saveRssConfig(@RequestBody  CLRssSource rss) throws MalformedURLException {
		return aerospikeDataService.addRssSource(rss);
	}
	
	@RequestMapping(value="/rss", method={RequestMethod.PUT})
	public CLRssSource updateRssConfig(@RequestBody  CLRssSource rss) throws MalformedURLException {
		return aerospikeDataService.updateRssSource(rss);
	}
	
	@RequestMapping(value="/rss/{url}", method={RequestMethod.DELETE})
	public void removeRssConfig(@PathVariable (value="url") String rssUrl) {
		aerospikeDataService.removeRss(rssUrl);
	}
	
	@RequestMapping("/rssList")
	public Collection<CLRssSource> listRssSource() {
		return aerospikeDataService.getRssSourceMap().values();
	}
	
	@RequestMapping("/rental")
	public List<CLRentPage> rentSearch(@RequestParam double lng, @RequestParam double lat, @RequestParam double radius, @RequestParam int hoursAgo){
		hoursAgo = hoursAgo > MAX_HOUR ? MAX_HOUR : hoursAgo;
		return aerospikeDataService.fetchPage(lng, lat, radius, new Date().getTime() - hoursAgo * 3600 * 1000);
	}
	
	@RequestMapping("/rssListArea")
	public Collection<CLRentPage> listRssSourceAreaGT(@RequestParam int a) {
		return aerospikeDataService.getRssSourceAreaGT(a);
	}
	
	
}

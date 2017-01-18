
package com.aerospike.springdata.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping("/hello")
	public List<CLRssSource> index() {
		CLRssSource dave = new CLRssSource("Iiny-url/Dave-01", 15, new Date(), "Matthews@gmail.com");
		CLRssSource donny = new CLRssSource("Iiny-url/Dave-02", 12, new Date(), "Macintire@gmail.com");
		CLRssSource oliver = new CLRssSource("Iiny-url/Oliver-01",15, new Date(),  "Matthews@gmail.com");
		CLRssSource carter = new CLRssSource("Iiny-url/Carter-01", 15, new Date(), "Beauford@gmail.com");
		CLRssSource boyd = new CLRssSource("Iiny-url/Boyd-01",15, new Date(),  "Tinsley@gmail.com");

		return Arrays.asList(oliver, dave, donny, carter, boyd);
	}
	
	@RequestMapping("/rssList")
	public Collection<CLRssSource> listRssSource() {
		return aerospikeDataService.getRssSourceMap().values();
	}

	
	@RequestMapping(path = "/people", method = RequestMethod.GET)
	public String getPeople() {
		return "done";
	}
	
	@RequestMapping("/rental")
	public List<CLRentPage> rentSearch(@RequestParam double lon, @RequestParam double lat, @RequestParam double radius, @RequestParam int hoursAgo){
		hoursAgo = hoursAgo > MAX_HOUR ? MAX_HOUR : hoursAgo;
		
		return aerospikeDataService.fetchPage(lon, lat, radius, new Date().getTime() - hoursAgo * 3600 * 1000);
	}
}

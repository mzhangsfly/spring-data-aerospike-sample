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

package com.aerospike.springdata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.aerospike.client.AerospikeClient;

@Configuration
@EnableAerospikeRepositories
@EnableTransactionManagement
public class AppAerospikeConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${spring.aerospike.hostname}")
	private String hostName;

	@Value("${spring.aerospike.port}")
	private int port;

	@Value("${spring.aerospike.namespace}")
	private String namespace;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	AerospikeClient aerospikeClient() {
		log.info("initializing aerospike client with: " + hostName + ":" + port);
		return new AerospikeClient(hostName, port);
	}

	@Bean
	AerospikeTemplate aerospikeTemplate() {
		return new AerospikeTemplate(aerospikeClient(), namespace);
	}

}

/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.lattice.discovery;

import org.cloudfoundry.receptor.client.ReceptorClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.cloud.lattice.LatticeProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.StringUtils;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableConfigurationProperties
public class LatticeDiscoveryClientConfiguration {

	@Autowired
	private LatticeProperties latticeProperties;

	@Bean
	public ReceptorService receptorService() {
		return new ReceptorService(receptorClient(), latticeProperties, latticeDiscoveryProperties());
	}

	@Bean
	public ReceptorClient receptorClient() {
		if (!StringUtils
				.hasText(latticeProperties.getReceptor().getUsername())) {
			return new ReceptorClient(latticeProperties.getReceptor()
					.getHost());
		}
		return new ReceptorClient(latticeProperties.getReceptor().getHost(),
				getClientRequestFactory());
	}

	private ClientHttpRequestFactory getClientRequestFactory() {
		String username = latticeProperties.getReceptor().getUsername();
		String password = latticeProperties.getReceptor().getPassword();
		return new TestRestTemplate(username, password).getRequestFactory();
	}

	@Bean
	public LatticeDiscoveryClient latticeDiscoveryClient() {
		return new LatticeDiscoveryClient(receptorService(), receptorClient());
	}

	@Bean
	public LatticeDiscoveryProperties latticeDiscoveryProperties() {
		return new LatticeDiscoveryProperties();
	}

}

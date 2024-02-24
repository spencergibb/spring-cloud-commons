/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.env;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.OpenFeatureAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marcin Grzejszczak
 */
@SpringBootTest(properties = {
		"my.boolean-flag=true",
		"my.string-flag=string-flag-value",
})
public class EnvironmentFeatureProviderTests {

	@Autowired
	private OpenFeatureAPI features;

	private Client client;

	@BeforeEach
	void init() {
		client = features.getClient();
	}

	@Test
	public void booleanFlagWorks() {
		FlagEvaluationDetails<Boolean> myBooleanFlag = client.getBooleanDetails("my.boolean-flag", false);
		assertThat(myBooleanFlag.getValue()).isTrue();
	}

	@Test
	public void stringFlagWorks() {
		FlagEvaluationDetails<String> myStringFlag = client.getStringDetails("my.string-flag", "");
		assertThat(myStringFlag.getValue()).isEqualTo("string-flag-value");
	}

	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	static class TestConfiguration {

		@Bean
		EnvironmentFeatureProvider environmentFeatureProvider(ConfigurableEnvironment env) {
			return new EnvironmentFeatureProvider(env);
		}

		@Bean
		OpenFeatureAPI openFeatureAPI(FeatureProvider featureProvider) {
			OpenFeatureAPI instance = OpenFeatureAPI.getInstance();
			instance.setProvider(featureProvider);
			return instance;
		}

	}

}

/*
 * Copyright 2002-2019 the original author or authors.
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
 package org.nim.yaml;


import org.nim.yaml.spring.core.io.Resource;
import org.nim.yaml.spring.util.CollectionFactory;

import  java.util.Properties;
public class YamlPropertiesFactoryBean extends YamlProcessor {

	private YamlPropertiesFactoryBean(){
	}
	public YamlPropertiesFactoryBean(Resource... resources){
		super.setResources(resources);

	}

	


	private Properties properties;
	
	public Properties getObject() {
		return (this.properties != null ? this.properties : createProperties());

	}

	public Class<?> getObjectType() {
		return Properties.class;

	}
	protected Properties createProperties() {
		Properties result = CollectionFactory.createStringAdaptingProperties();
		process((properties, map) -> result.putAll(properties));
		return result;

	}
	public void afterPropertiesSet() throws Exception {
		this.properties = createProperties();

	}

}


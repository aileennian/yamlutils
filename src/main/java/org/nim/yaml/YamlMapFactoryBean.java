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
import  java.util.LinkedHashMap;

import java.util.Map;
public class YamlMapFactoryBean extends YamlProcessor {
	


	private Map<String, Object> map;


	private YamlMapFactoryBean(){
	}
	public YamlMapFactoryBean(Resource... resources){
		super.setResources(resources);

	}
	
	public Map<String, Object> getObject() {
		return (this.map != null ? this.map : createMap());

	}
	public Class<?> getObjectType() {
		return Map.class;

	}

	protected Map<String, Object> createMap() {
		Map<String, Object> result = new LinkedHashMap<>();

		process((properties, map) -> merge(result, map));

		return result;

	}
	@SuppressWarnings({"rawtypes", "unchecked"}
)


	private void merge(Map<String, Object> output, Map<String, Object> map) {
		map.forEach((key, value) -> {
			Object existing = output.get(key);

			if (value instanceof Map && existing instanceof Map) {
				Map<String, Object> result = new LinkedHashMap<>((Map<String, Object>) existing);

				merge(result, (Map) value);

				output.put(key, result);

			}

			else {
				output.put(key, value);

			}

		}
);

	}

	public void afterPropertiesSet() throws Exception {
		this.map = createMap();

	}

}


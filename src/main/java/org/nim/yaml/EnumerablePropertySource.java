/*
 * Copyright 2002-2020 the original author or authors.
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


import org.nim.yaml.spring.util.ObjectUtils;

public abstract class EnumerablePropertySource<T> extends PropertySource<T> {
	public EnumerablePropertySource(String name, T source) {
		super(name, source);

	}

	protected EnumerablePropertySource(String name) {
		super(name);

	}

	@Override
	public boolean containsProperty(String name) {
		return ObjectUtils.containsElement(getPropertyNames(), name);

	}

	public abstract String[] getPropertyNames();
}


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


import  org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nim.yaml.spring.util.Assert;
import org.nim.yaml.spring.util.ObjectUtils;

public abstract class PropertySource<T> {	protected final Log logger = LogFactory.getLog(getClass());
	protected final String name;
	protected final T source;

	//protected final PropertiesPropertySource source;
	


		/**
		 * Create a new {@code PropertySource}	 with the given name and source object.
		 * @param name the associated name
		 * @param source the source object
		 */
		public PropertySource(String name, T source) {
			Assert.hasText(name, "Property source name must contain at least one character");
			Assert.notNull(source, "Property source must not be null");
			this.name = name;
			this.source = source;

		}



		/**
		 * Create a new {@code PropertySource}	 with the given name and with a new
		 * {@code Object}	 instance as the underlying source.
		 * <p>Often useful in testing scenarios when creating anonymous implementations
		 * that never query an actual source but rather return hard-coded values.
		 */
		@SuppressWarnings("unchecked")
		public PropertySource(String name) {
			this(name, (T) new Object());

		}




		/**
		 * Return the name of this {@code PropertySource}	.
		 */
		public String getName() {
			return this.name;

		}



		/**
		 * Return the underlying source object for this {@code PropertySource}	.
		 */
		public T getSource() {
			return this.source;

		}

		public boolean containsProperty(String name) {
			return (getProperty(name) != null);

		}
		public abstract Object getProperty(String name);

		@Override
		public boolean equals(Object other) {
			return (this == other || (other instanceof PropertySource &&
					ObjectUtils.nullSafeEquals(this.name, ((PropertySource<?>) other).name)));

		}

		@Override
		public int hashCode() {
			return ObjectUtils.nullSafeHashCode(this.name);

		}

		@Override
		public String toString() {
			if (logger.isDebugEnabled()) {
				return getClass().getSimpleName() + "@" + System.identityHashCode(this) +
						" {name='" + this.name + "', properties=" + this.source + "}";

			}else {
				return getClass().getSimpleName() + " {name='" + this.name + "'}";

			}

		}
		public static PropertySource<?> named(String name) {
			return new ComparisonPropertySource(name);

		}
		public static class StubPropertySource extends PropertySource<Object> {		public StubPropertySource(String name) {
				super(name, new Object());

		}
		


		/**
		 * Always returns {@code null}.
		 */
		@Override
		public String getProperty(String name) {
			return null;

		}

	}

	


	/**
	 * A {@code PropertySource} implementation intended for collection comparison	 * purposes.
	 *
	 * @see PropertySource#named(String)
	 */
		static class ComparisonPropertySource extends StubPropertySource {

		private static final String USAGE_ERROR ="ComparisonPropertySource instances are for use with collection comparison only";
		public ComparisonPropertySource(String name) {
			super(name);

		}
	
		@Override
		public Object getSource() {
			throw new UnsupportedOperationException(USAGE_ERROR);

		}

		@Override
		public boolean containsProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);

		}

		@Override
		public String getProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);

		}

	}
}


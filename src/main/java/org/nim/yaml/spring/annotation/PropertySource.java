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
 package org.nim.yaml.spring.annotation;

import org.nim.yaml.spring.core.io.support.PropertySourceFactory;
import java.lang.annotation.*;


/**
* Annotation providing a convenient and declarative mechanism for adding a*
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {
     String name() default "";


     /**
      * Indicate the resource location(s) of the properties file to be loaded.
      */
     String[] value();

     /**
      * Indicate if failure to find the a {@link #value() property resource} should be
      * ignored.
      */
     boolean ignoreResourceNotFound() default false;


    /**
     * A specific character encoding for the given resources, e.g. "UTF-8".
     * @since 4.3
     */
    String encoding() default "";

    /**
     */
    Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;
}


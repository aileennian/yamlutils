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
 package org.nim.yaml.spring.annotation;

import java.lang.annotation.*;


/**
* Annotation at the field or method/constructor parameter level
* that indicates a default value expression for the affected argument.
*
* <p>Typically used for expression-driven dependency injection. Also supported
* for dynamic resolution of handler method parameters, e.g. in Spring MVC.
*
* <p>A common use case is to assign default field values using
* <code>#{systemProperties.myProp}
</code> style expressions.
*
* @author Juergen Hoeller
* @since 3.0
*/
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE}
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {


/**
 * The actual value expression &mdash;
for example, <code>#{systemProperties.myProp}
</code>.
 */
String value();
}


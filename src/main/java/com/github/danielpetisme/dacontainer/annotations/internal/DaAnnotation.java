/**
 * Copyright (C) 2011 Daniel PETISME <daniel.petisme@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.danielpetisme.dacontainer.annotations.internal;

import java.lang.reflect.AccessibleObject;

public interface DaAnnotation {

	/**
	 * Applying a treatment on a accesible object (Field or Method)
	 * 
	 * @param annotedObject
	 *            a FIELD or METHOD
	 */
	public <T> T apply(T instance, AccessibleObject annotedObject)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * 
	 * @param annotedObject
	 *            a CONSTRUCTOR
	 * @return an instance
	 */
	public <T> T apply(AccessibleObject annotedObject);
}

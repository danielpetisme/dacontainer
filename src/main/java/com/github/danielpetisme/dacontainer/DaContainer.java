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
package com.github.danielpetisme.dacontainer;

public interface DaContainer {

	/**
	 * Bind an a Class (an implementation) with its contract (an interface)
	 * 
	 * @param contract
	 *            minimal set of operations the implementation must have
	 * @param clazz
	 *            the implementation
	 */
	public void bind(Class<?> contract, Class<?> clazz);

	/**
	 * To store a class without interface. Is a shortcut to bind(clazz,clazz);
	 * 
	 * @param clazz
	 */

	public void bind(Class<?> clazz);

	/**
	 * Retrieve an instance for the given contract
	 * 
	 * @param contract
	 *            the needed operations
	 * @return the instance or null
	 */
	public <T> T getInstance(Class<?> contract);
}

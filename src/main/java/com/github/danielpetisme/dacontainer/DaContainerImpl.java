/**
 * Copyright (C) 2010 Daniel <daniel.petisme@gmail.com>
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaContainerImpl implements DaContainer {

	private final static String TAG = DaContainerImpl.class.getName();

	private final static Logger LOG = Logger.getLogger(TAG);

	public static DaContainer INSTANCE = new DaContainerImpl();

	private Map<Class<?>, Class<?>> mapping;

	private DaContainerImpl() {

		mapping = new HashMap<Class<?>, Class<?>>();
		LOG.log(Level.FINE, "Mapping map created");
	}

	public void bind(Class<?> contract, Class<?> clazz) {
		checkNotNull(contract, "The contract cannot be null");
		checkNotNull(clazz, "The class cannot be null");
		checkArgument(contract.isInterface(), "%s is not an interface",
				contract);
		checkArgument(Arrays.asList(clazz.getInterfaces()).contains(contract),
				"%s is not an implementation of %s", clazz, contract);

		// Old contrac - clazz is replaced by the new one
		mapping.put(contract, clazz);

	}

	public <T> T getInstance(Class<?> contract) {
		checkNotNull(contract, "The contract cannot be null");
		checkArgument(mapping.containsKey(contract), "Unbounded interface");

		Class<?> clazz = mapping.get(contract);

		T instance = null;
		try {
			instance = (T) clazz.newInstance();
		} catch (InstantiationException e) {
			LOG.log(Level.SEVERE, "Instantiation impossible", e);
		} catch (IllegalAccessException e) {
			LOG.log(Level.SEVERE, "Illegal Access", e);
		}
		return instance;
	}
}

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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.danielpetisme.dacontainer.DaContainerImpl;

public class InjectImpl implements DaAnnotation {

	private final static String TAG = InjectImpl.class.getName();

	private final static Logger LOG = Logger.getLogger(TAG);

	/**
	 * Inject instance in a field
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public <T> T apply(T instance, AccessibleObject accessibleObject)
			throws IllegalArgumentException, IllegalAccessException {
		checkNotNull(instance);
		checkNotNull(accessibleObject);
		checkArgument(accessibleObject instanceof Field
				|| accessibleObject instanceof Method,
				"The argument can not be treated is not a Field, a Method or a Constructor");
		T injectedInstance = null;
		if (accessibleObject instanceof Field) {
			Field field = (Field) accessibleObject;
			injectedInstance = apply(instance, field);
		} else if (accessibleObject instanceof Method) {
			Method method = (Method) accessibleObject;
			injectedInstance = apply(instance, method);
		}

		return injectedInstance;

	}

	private <T> T apply(T instance, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = field.getType();
		Object dependency = DaContainerImpl.INSTANCE.getInstance(type);
		field.set(instance, dependency);
		LOG.log(Level.FINE, "Injecting  {0} on field {1}", new Object[] {
				dependency, field });

		return instance;
	}

	private <T> T apply(T instance, Method method)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?>[] types = method.getParameterTypes();
		List<Object> parameters = new ArrayList<Object>();
		for (Class<?> type : types) {
			Object dependency = DaContainerImpl.INSTANCE.getInstance(type);
			checkNotNull(dependency, "No bindind found for {0}",
					new Object[] { type });
			parameters.add(dependency);
		}

		try {
			method.invoke(instance, parameters.toArray());
			LOG.log(Level.FINE, "Injecting  {0} on method {1}", new Object[] {
					parameters.toArray(), method });
		} catch (InvocationTargetException e) {
			LOG.log(Level.SEVERE, "Methods injection", e);
		}

		return instance;

	}

	@SuppressWarnings("unchecked")
	public <T> T apply(AccessibleObject annotedObject) {
		T instance = null;
		if (annotedObject instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) annotedObject;
			Class<?>[] types = constructor.getParameterTypes();
			List<Object> parameters = new ArrayList<Object>();
			for (Class<?> type : types) {
				Object dependency = DaContainerImpl.INSTANCE.getInstance(type);
				checkNotNull(dependency, "No bindind found for {0}",
						new Object[] { type });
				parameters.add(dependency);
			}

			try {
				instance = (T) constructor.newInstance(parameters.toArray());
				LOG.log(Level.FINE, "Injecting  {0} on constructor {1}",
						new Object[] { parameters.toArray(), constructor });
			} catch (IllegalArgumentException e) {
				LOG.log(Level.SEVERE, "Constructor injection", e);
			} catch (InstantiationException e) {
				LOG.log(Level.SEVERE, "Constructor injection", e);
			} catch (IllegalAccessException e) {
				LOG.log(Level.SEVERE, "Constructor injection", e);
			} catch (InvocationTargetException e) {
				LOG.log(Level.SEVERE, "Constructor injection", e);
			}
		}

		return instance;
	}
}

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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
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
	public <T> void apply(T instance, Member member)
			throws IllegalArgumentException, IllegalAccessException {
		checkNotNull(instance);
		checkNotNull(member);
		checkArgument(member instanceof Field,
				"The argument is not an instance of field");
		if (member instanceof Field) {
			Field field = (Field) member;
			apply(instance, field);
		}

	}

	private <T> void apply(T instance, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = field.getType();
		Object dependency = DaContainerImpl.INSTANCE.getInstance(type);
		field.set(instance, dependency);
		LOG.log(Level.FINE, "Injecting  {0} on field {1}", new Object[] {
				dependency, field });
	}
}

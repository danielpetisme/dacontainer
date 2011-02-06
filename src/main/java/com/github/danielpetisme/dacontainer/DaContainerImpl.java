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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.danielpetisme.dacontainer.annotations.Inject;
import com.github.danielpetisme.dacontainer.annotations.internal.DaAnnotation;
import com.github.danielpetisme.dacontainer.annotations.internal.InjectImpl;
import com.google.common.base.Predicate;
import com.googlecode.functionalcollections.Block;
import com.googlecode.functionalcollections.FunctionalIterables;

public class DaContainerImpl implements DaContainer {

	private final static String TAG = DaContainerImpl.class.getName();

	private final static Logger LOG = Logger.getLogger(TAG);

	public static DaContainer INSTANCE = new DaContainerImpl();

	private Map<Class<?>, Class<?>> mapping;

	private List<Class<? extends Annotation>> managedAnnotations;

	private DaContainerImpl() {

		mapping = new HashMap<Class<?>, Class<?>>();
		LOG.log(Level.FINE, "Mapping map created");

		managedAnnotations = new ArrayList<Class<? extends Annotation>>();
		managedAnnotations.add(Inject.class);
		bind(Inject.class, InjectImpl.class);

	}

	public void bind(Class<?> contract, Class<?> clazz) {
		checkNotNull(contract, "The contract cannot be null");
		checkNotNull(clazz, "The class cannot be null");

		// Old contract - clazz is replaced by the new one
		mapping.put(contract, clazz);

	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<?> contract) {
		checkNotNull(contract, "The contract cannot be null");
		checkArgument(mapping.containsKey(contract), "Unbounded interface");

		Class<?> clazz = mapping.get(contract);

		T instance = null;
		try {
			instance = (T) clazz.newInstance();
			dependencyInjection(instance);
		} catch (InstantiationException e) {
			LOG.log(Level.SEVERE, "Instantiation impossible", e);
		} catch (IllegalAccessException e) {
			LOG.log(Level.SEVERE, "Illegal Access", e);
		}
		return instance;
	}

	private <T> void dependencyInjection(final T instance) {

		// Filter the fields to keep only annotated fields.
		Predicate<Field> predicateAnnotedField = new Predicate<Field>() {
			public boolean apply(Field candidateField) {
				return candidateField.getAnnotations().length > 0;
			}
		};

		// Hack to have accessible annotated fields
		Block<Field> setAccessible = new Block<Field>() {
			public void apply(Field annotedField) {
				annotedField.setAccessible(true);
			}
		};

		// filter annotated fields and set them accessibles
		Iterable<Field> annotedFields = (Iterable<Field>) FunctionalIterables
				.make(instance.getClass().getDeclaredFields())
				.filter(predicateAnnotedField).each(setAccessible);

		// Manage the injection
		inject(instance, annotedFields);

	}

	/**
	 * @param instance
	 * @param annotedFields
	 */
	private <T> void inject(final T instance, Iterable<Field> annotedFields) {
		for (Class<? extends Annotation> annotation : managedAnnotations) {
			final Class<? extends Annotation> clazz = annotation;

			// Apply the right treatment respectively to the annotation present
			FunctionalIterables.make(annotedFields)
					.filter(new Predicate<Field>() {
						public boolean apply(Field annotedField) {
							return annotedField.isAnnotationPresent(clazz);
						}
					}).each(new Block<Field>() {
						public void apply(Field annotedField) {
							DaAnnotation annotation = getInstance(clazz);
							try {
								annotation.apply(instance, annotedField);
							} catch (IllegalArgumentException e) {
								LOG.log(Level.SEVERE, "Illegal Argument", e);
							} catch (IllegalAccessException e) {
								LOG.log(Level.SEVERE, "Illegal Access", e);
							}
						}
					});
		}
	}

}

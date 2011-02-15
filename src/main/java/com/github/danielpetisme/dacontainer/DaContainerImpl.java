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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.danielpetisme.dacontainer.annotations.AnnotationInjection;
import com.github.danielpetisme.dacontainer.annotations.Inject;
import com.github.danielpetisme.dacontainer.annotations.Named;
import com.github.danielpetisme.dacontainer.annotations.internal.AnnotationInjectionImpl;
import com.github.danielpetisme.dacontainer.annotations.internal.DaAnnotation;
import com.github.danielpetisme.dacontainer.annotations.internal.InjectImpl;
import com.github.danielpetisme.dacontainer.annotations.internal.NamedImpl;
import com.google.common.base.Predicate;
import com.googlecode.functionalcollections.Block;
import com.googlecode.functionalcollections.FunctionalIterables;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DaContainerImpl implements DaContainer {

	private final static String TAG = DaContainerImpl.class.getName();

	private final static Logger LOG = Logger.getLogger(TAG);

	public static DaContainer INSTANCE = new DaContainerImpl();

	private Map<Class<?>, Class<?>> mapping;

	private Map<String, Object> constants;

	private List<Class<? extends Annotation>> managedAnnotations;

	// Filter the fields to keep only annotated fields.
	private static final Predicate<AccessibleObject> isAnnotedAccessibleObject = new Predicate<AccessibleObject>() {
		public boolean apply(AccessibleObject candidateField) {
			return candidateField.getAnnotations().length > 0;
		}
	};

	// Hack to have accessible annotated fields
	private static final Block<AccessibleObject> setAccessible = new Block<AccessibleObject>() {
		public void apply(AccessibleObject annotedField) {
			annotedField.setAccessible(true);
		}
	};

	private DaContainerImpl() {

		mapping = new HashMap<Class<?>, Class<?>>();
		LOG.log(Level.FINE, "Mapping map created");

		constants = new HashMap<String, Object>();
		LOG.log(Level.FINE, "Constants map created");

		managedAnnotations = new ArrayList<Class<? extends Annotation>>();
		managedAnnotations.add(Inject.class);
		managedAnnotations.add(Named.class);
		bind(Inject.class, InjectImpl.class);
		bind(Named.class, NamedImpl.class);

	}

	public void bind(Class<?> contract, Class<?> clazz) {
		checkNotNull(contract, "The contract cannot be null");
		checkNotNull(clazz, "The class cannot be null");

		// Old contract - clazz is replaced by the new one
		mapping.put(contract, clazz);

	}

	public void bind(Class<?> clazz) {
		bind(clazz, clazz);

	}

	public <T> T getInstance(Class<?> contract) {
		checkNotNull(contract, "The contract cannot be null");
		checkArgument(mapping.containsKey(contract), "Unbounded interface %s",
				contract);

		Class<?> clazz = mapping.get(contract);

		return constructorDependencyInjection(clazz);
	}

	/**
	 * Search if any depencies have to be injected and construct the object
	 * 
	 * @param toConstruct
	 *            The class to construct
	 * @return an instance or null
	 */
	@SuppressWarnings("unchecked")
	private <T> T constructorDependencyInjection(Class<?> toConstruct) {
		checkNotNull(toConstruct);
		T instance = null;

		// Search all the constuctor with al least one managed annotation
		List<Constructor<?>> constructors = FunctionalIterables
				.make(toConstruct.getConstructors())
				.filter(new Predicate<Constructor<?>>() {

					public boolean apply(final Constructor<?> constructor) {
						return FunctionalIterables
								.make(managedAnnotations)
								.any(new Predicate<Class<? extends Annotation>>() {

									public boolean apply(
											Class<? extends Annotation> annotation) {
										return constructor
												.isAnnotationPresent(annotation);
									}
								});
					}
				}).toList();

		// constuctor with DaAnnotation
		if (constructors.size() > 0) {
			Constructor<?> constructor = constructors.get(0);
			for (Class<? extends Annotation> annotation : managedAnnotations) {
				final Class<? extends Annotation> clazz = annotation;

				if (constructor.isAnnotationPresent(clazz)) {
					DaAnnotation daAnnotation = getInstance(clazz);
					instance = daAnnotation.apply(constructor);
				}
			}
		} else {
			try {
				instance = (T) toConstruct.newInstance();
			} catch (InstantiationException e) {
				LOG.log(Level.SEVERE, "Instantiation impossible", e);
			} catch (IllegalAccessException e) {
				LOG.log(Level.SEVERE, "Illegal Access", e);
			}

		}

		if (instance != null) {
			// Once we have constructed the object, we inject the members (FIELD
			// & METHOD)
			dependencyInjection(instance);
		}

		return instance;
	}

	/**
	 * Inject fields and methods
	 * 
	 * @param instance
	 */
	private <T> void dependencyInjection(final T instance) {
		inject(instance, filterFields(instance));
		inject(instance, filterMethods(instance));
	}

	private <T> Iterable<Field> filterFields(final T instance) {

		// filter annotated fields and set them accessibles
		return FunctionalIterables
				.make(instance.getClass().getDeclaredFields())
				.filter(isAnnotedAccessibleObject).each(setAccessible);
	}

	private <T> Iterable<Method> filterMethods(final T instance) {

		// filter annotated fields and set them accessibles
		return FunctionalIterables
				.make(instance.getClass().getDeclaredMethods())
				.filter(isAnnotedAccessibleObject).each(setAccessible);
	}

	/**
	 * apply every annotation on the current object
	 * 
	 * @param <T>
	 * @param instance
	 * @param elements
	 */
	private <T> void inject(final T instance,
			Iterable<? extends AccessibleObject> elements) {
		for (Class<? extends Annotation> annotation : managedAnnotations) {
			final Class<? extends Annotation> clazz = annotation;

			// Apply the right treatment respectively to the annotation present
			FunctionalIterables.make(elements)
					.filter(new Predicate<AccessibleObject>() {

						public boolean apply(AccessibleObject object) {
							return object.isAnnotationPresent(clazz);
						}
					}).each(new Block<AccessibleObject>() {
						public void apply(AccessibleObject annotedField) {
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

	@Override
	public void bindConstant(String constantName, Object constantValue) {
		checkNotNull(constantName, "The constantName cannot be null");
		checkNotNull(constantValue, "The constantValue cannot be null");

		constants.put(constantName, constantValue);
	}

	public Object getConstant(String constantName) {
		checkNotNull(constantName, "The constantName cannot be null");
		checkArgument(constants.containsKey(constantName),
				"No binding founded for constantName : {0}", constantName);
		Object value = null;
		value = constants.get(constantName);
		return value;
	}

	@Override
	public void bindAnnotation(Class<? extends Annotation> annotation,
			String value) {
		checkNotNull(annotation, "The annotation cannot be null");
		checkNotNull(value, "The value cannot be null");
		checkArgument(
				annotation.isAnnotationPresent(AnnotationInjection.class),
				"The annotation {0} must be annoted with AnnotationInjection",
				annotation);

		constants.put(annotation.getName(), value);
		managedAnnotations.add(annotation);
		bind(annotation, AnnotationInjectionImpl.class);
	}
}

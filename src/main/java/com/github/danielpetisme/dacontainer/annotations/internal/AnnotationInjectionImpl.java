package com.github.danielpetisme.dacontainer.annotations.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.danielpetisme.dacontainer.DaContainerImpl;
import com.github.danielpetisme.dacontainer.annotations.AnnotationInjection;
import com.github.danielpetisme.dacontainer.annotations.Named;

public class AnnotationInjectionImpl implements DaAnnotation {

	private final static String TAG = NamedImpl.class.getName();

	private final static Logger LOG = Logger.getLogger(TAG);

	@Override
	public <T> T apply(T instance, AccessibleObject annotedObject)
			throws IllegalArgumentException, IllegalAccessException {
		checkNotNull(instance);
		checkNotNull(annotedObject);
		checkArgument(annotedObject instanceof Field,
				"The annotedObject is not a field");
		T instanceInjected = null;
		if (annotedObject instanceof Field) {
			Field field = (Field) annotedObject;
			instanceInjected = apply(instance, field);
		}
		return instanceInjected;
	}

	private <T> T apply(T instance, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		Annotation[] annotations = field.getAnnotations();
		Annotation customAnnotation = null;
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().isAnnotationPresent(AnnotationInjection.class)) {
				customAnnotation = annotation;
				break;
			}
		}
		Object value = DaContainerImpl.INSTANCE.getConstant(customAnnotation.annotationType().getName());
		field.set(instance, value);
		LOG.log(Level.FINE, "Injecting  value {0} on field {1}", new Object[] {
				value, field });

		return instance;
	}

	@Override
	public <T> T apply(AccessibleObject annotedObject) {
		// TODO Auto-generated method stub
		return null;
	}

}

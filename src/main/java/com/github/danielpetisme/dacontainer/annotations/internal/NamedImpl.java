package com.github.danielpetisme.dacontainer.annotations.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.danielpetisme.dacontainer.DaContainerImpl;
import com.github.danielpetisme.dacontainer.annotations.Named;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NamedImpl implements DaAnnotation {

	private final static String TAG = NamedImpl.class.getName();

	private final static Logger LOG = Logger.getLogger(TAG);

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

	public <T> T apply(T instance, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		String constantName = field.getAnnotation(Named.class).value();
		Object value = DaContainerImpl.INSTANCE.getConstant(constantName);
		field.set(instance, value);
		LOG.log(Level.FINE, "Injecting  value {0} on field {1}", new Object[] {
				value, field });

		return instance;

	}

	public <T> T apply(AccessibleObject annotedObject) {
		// TODO Auto-generated method stub
		return null;
	}

}

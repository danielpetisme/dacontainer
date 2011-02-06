package com.github.danielpetisme.test.dacontainer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.danielpetisme.dacontainer.DaContainer;
import com.github.danielpetisme.dacontainer.DaContainerImpl;
import com.github.danielpetisme.dacontainer.exception.UnboundedInterfaceException;

public class DaContainerImplTest {

	private DaContainer tested;

	@Before
	public void setUp() throws Exception {
		tested = DaContainerImpl.INSTANCE;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testSingleton() {
		DaContainer instance1 = DaContainerImpl.INSTANCE;
		DaContainer instance2 = DaContainerImpl.INSTANCE;

		assertThat(instance1, is(sameInstance(instance2)));
	}

	@Test
	public final void testRegister() {
		tested.bind(List.class, ArrayList.class);
		assertTrue(tested.getInstance(List.class) instanceof List);

		// Must throw an IllegalArgumentException
		// Try to bin a class with something
		try {

			tested.bind(ArrayList.class, String.class);
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is(not(nullValue())));
		}

		// Must throw an IllegalArgumentException
		// the implementation is not an implementation of the interface
		try {
			tested.bind(Map.class, String.class);
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is(not(nullValue())));
		}
		
		// Must throw a NPE
		try {
			tested.bind(null, String.class);
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), is(not(nullValue())));
		}
		
		// Must throw a NPE
		try {
			tested.bind(Map.class, null);
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), is(not(nullValue())));
		}

	}

	@Test
	public final void testGetInstance() {

		tested.bind(List.class, ArrayList.class);

		assertTrue(tested.getInstance(List.class) instanceof List);
		assertTrue(tested.getInstance(List.class) instanceof ArrayList);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetInstanceUnBoundedInterface() {
		// must throw a IllegalArgumentException
		tested.getInstance(Map.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetInstanceIllegal() {
		// must throw an IllegalArgumentException
		tested.getInstance(ArrayList.class);
	}

}

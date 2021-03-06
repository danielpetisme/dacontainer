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

public class DaContainerImplTestBasics {

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
	public final void testBind() {
		tested.bind(List.class, ArrayList.class);
		assertTrue(tested.getInstance(List.class) instanceof List);

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

		tested.bind(String.class);
		assertTrue(tested.getInstance(String.class) instanceof String);

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
	
}

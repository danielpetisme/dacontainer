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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.danielpetisme.dacontainer.DaContainer;
import com.github.danielpetisme.dacontainer.DaContainerImpl;
import com.github.danielpetisme.dacontainer.annotations.Inject;

public class DaContainerImplTestInjection {

	private DaContainer tested;

	@Before
	public void setUp() throws Exception {
		tested = DaContainerImpl.INSTANCE;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testInjectionField() throws Exception {
		tested.bind(List.class, ArrayList.class);
		tested.bind(Person.class, PersonImplField.class);

		assertTrue(PersonImplField.class.getField("friends")
				.isAnnotationPresent(Inject.class));
		Person instance = tested.getInstance(Person.class);

		assertThat(instance.getFriends(), is(not(nullValue())));
	}

	@Test
	public final void testInjectionMethod() throws Exception {
		tested.bind(List.class, ArrayList.class);
		tested.bind(Person.class, PersonImplMethod.class);

		assertTrue(PersonImplMethod.class.getDeclaredMethod("setFriends",
				List.class).isAnnotationPresent(Inject.class));
		Person instance = tested.getInstance(Person.class);

		assertThat(instance.getFriends(), is(not(nullValue())));
	}

	@Test
	public final void testInjectionConstructor() throws Exception {
		tested.bind(List.class, ArrayList.class);
		tested.bind(Person.class, PersonImplConstructor.class);

		assertTrue(PersonImplConstructor.class.getConstructor(List.class)
				.isAnnotationPresent(Inject.class));
		Person instance = tested.getInstance(Person.class);

		assertThat(instance.getFriends(), is(not(nullValue())));
		assertTrue(instance.getFriends() instanceof List);
		assertThat(instance.getFriends().size(), is(0));
	}

	@SuppressWarnings("rawtypes")
	public static interface Person {

		public List getFriends();

		public void setFriends(List friends);

	}

	@SuppressWarnings("rawtypes")
	public static class PersonImplMethod implements Person {

		private List friends;

		public List getFriends() {
			return friends;
		}

		@Inject
		public void setFriends(List friends) {
			this.friends = friends;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class PersonImplField implements Person {

		@Inject
		public List friends;

		public List getFriends() {
			return friends;
		}

		public void setFriends(List friends) {
			throw new UnsupportedOperationException("Useless implementation");

		}

	}

	@SuppressWarnings("rawtypes")
	public static class PersonImplConstructor implements Person {

		private List friends;

		@Inject
		public PersonImplConstructor(List friends) {
			this.friends = friends;
		}

		public List getFriends() {
			return this.friends;
		}

		public void setFriends(List friends) {
			throw new UnsupportedOperationException("Useless implementation");
		}

	}
}

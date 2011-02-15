package com.github.danielpetisme.test.dacontainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import com.github.danielpetisme.dacontainer.DaContainer;
import com.github.danielpetisme.dacontainer.DaContainerImpl;
import com.github.danielpetisme.dacontainer.annotations.AnnotationInjection;

public class DaContainerImplTestAnnotationInjection {

	private DaContainer tested;

	@Before
	public void setUp() throws Exception {
		tested = DaContainerImpl.INSTANCE;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAnnotationBinding() throws Exception {

		tested.bindAnnotation(ServerName.class, "localhost");
		tested.bind(MyServer.class);

		MyServer server = tested.getInstance(MyServer.class);

		assertThat(server.getServerName(), is("localhost"));

		try {
			tested.bindAnnotation(null, "useless");
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), is(not(nullValue())));
		}
	}

	@Test
	public void testInvalidAnnotation() throws Exception {
		try {
			tested.bindAnnotation(InvalidAnnotation.class, "useless");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is(not(nullValue())));
		}
	}

	public static @interface InvalidAnnotation {
	}

	@AnnotationInjection
	@Target(value = ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ServerName {
	}

	public static class MyServer {

		@ServerName
		private String serverName;

		public String getServerName() {
			return this.serverName;
		}
	}
}

package com.github.danielpetisme.test.dacontainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.danielpetisme.dacontainer.DaContainer;
import com.github.danielpetisme.dacontainer.DaContainerImpl;
import com.github.danielpetisme.dacontainer.annotations.Inject;
import com.github.danielpetisme.dacontainer.annotations.Named;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

public class DaContainerImplTestNamedInjection {

	private DaContainer tested;

	@Before
	public void setUp() throws Exception {
		tested = DaContainerImpl.INSTANCE;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNamedInjection() {

		tested.bind(Terminal.class);
		tested.bindConstant("terminal.prompt", "$>");

		Terminal term = tested.getInstance(Terminal.class);

		String termPrompt = term.printPrompt("Foo");

		assertThat(termPrompt, is("Foo $>"));
	}

	@Test
	public void testNamedInjectionCascade() {

		tested.bind(Terminal.class);
		tested.bindConstant("terminal.prompt", "$>");
		tested.bindConstant("username", "Foo");
		tested.bind(OperatingSystem.class);

		OperatingSystem os = tested.getInstance(OperatingSystem.class);
		assertThat(os.openTerminal(), is("Foo $>"));
	}

	@Test
	public void testNamedInjectionPrimitiveTypes() {
		tested.bind(H2G2.class);
		tested.bindConstant("answer", 42);

		H2G2 EarthComputer = tested.getInstance(H2G2.class);
		assertThat(EarthComputer.getTheAnswer(), is(42));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNamedInjectionUnboundedName() {

		tested.getConstant("Unbounded");
	}

	public static class H2G2 {

		@Named("answer")
		private int theAnswer;

		public int getTheAnswer() {
			return this.theAnswer;
		}
	}

	public static final class Terminal {

		@Named("terminal.prompt")
		private String prompt;

		public String printPrompt(String username) {
			return String.format("%s %s", username, prompt);
		}
	}

	public static final class OperatingSystem {

		@Inject
		private Terminal terminal;

		@Named("username")
		private String username;

		public String openTerminal() {
			return terminal.printPrompt(username);
		}
	}

}

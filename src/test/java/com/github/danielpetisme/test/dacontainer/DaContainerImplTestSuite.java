package com.github.danielpetisme.test.dacontainer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { DaContainerImplTestBasics.class,
		DaContainerImplTestInjection.class,
		DaContainerImplTestNamedInjection.class , DaContainerImplTestAnnotationInjection.class})
		
public class DaContainerImplTestSuite {

}

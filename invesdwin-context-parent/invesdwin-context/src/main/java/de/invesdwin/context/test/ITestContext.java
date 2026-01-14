package de.invesdwin.context.test;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

public interface ITestContext extends ConfigurableApplicationContext, BeanDefinitionRegistry, ITestContextSetup {

}

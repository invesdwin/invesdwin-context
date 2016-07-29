package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@Named
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Immutable
class TestBeanToo implements ITestBean {

}

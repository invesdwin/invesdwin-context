package de.invesdwin.context.beans.internal;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.aspectj.AnnotationAsyncExecutionAspect;

/**
 * As a workaround for https://jira.springsource.org/browse/SPR-8772 and as a fix for another problem that seems to not
 * make the setter be called.
 * 
 * @author subes
 * 
 */
@Immutable
@Named
public class AsyncAnnotationEnabler implements InitializingBean {

    @Named("asyncTaskExecutor")
    @Inject
    private AsyncTaskExecutor asyncTaskExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        final AnnotationAsyncExecutionAspect aspect = AnnotationAsyncExecutionAspect.aspectOf();
        aspect.setExecutor(asyncTaskExecutor);
    }

}

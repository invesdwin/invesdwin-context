package de.invesdwin.context.integration.retry.internal;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.support.RetryTemplate;

import de.invesdwin.context.integration.retry.ExceptionCauseRetryPolicy;

@NotThreadSafe
@Named
public class ExceptionCauseRetryTemplate extends RetryTemplate implements FactoryBean<ExceptionCauseRetryTemplate> {

    public static final ExceptionCauseRetryTemplate INSTANCE = new ExceptionCauseRetryTemplate();

    //  <bean id="exceptionRetryTemplate" class="org.springframework.retry.support.RetryTemplate">
    //        <property name="backOffPolicy">
    //            <bean
    //                class="org.springframework.retry.backoff.ExponentialBackOffPolicy">
    //                <property name="initialInterval"
    //                    value="#{new de.invesdwin.util.time.duration.Duration(1L, T(de.invesdwin.util.time.fdate.FTimeUnit).SECONDS).longValue(T(de.invesdwin.util.time.fdate.FTimeUnit).MILLISECONDS)}" />
    //                <property name="maxInterval"
    //                    value="#{new de.invesdwin.util.time.duration.Duration(15L, T(de.invesdwin.util.time.fdate.FTimeUnit).MINUTES).longValue(T(de.invesdwin.util.time.fdate.FTimeUnit).MILLISECONDS)}" />
    //            </bean>
    //        </property>
    //        <property name="retryPolicy">
    //            <bean
    //                class="de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy">
    //            </bean>
    //        </property>
    //    </bean>
    public ExceptionCauseRetryTemplate() {
        setBackOffPolicy(ExceptionCauseBackOffPolicy.INSTANCE);
        setRetryPolicy(ExceptionCauseRetryPolicy.INSTANCE);
        registerListener(new RetryListenerSupport() {
            @Override
            public <T, E extends Throwable> boolean open(final RetryContext context,
                    final RetryCallback<T, E> callback) {
                if (callback instanceof ExceptionCauseRetryCallback) {
                    final ExceptionCauseRetryCallback<?> cCallback = (ExceptionCauseRetryCallback) callback;
                    cCallback.open(context);
                }
                return true;
            }
        });
    }

    @Override
    public ExceptionCauseRetryTemplate getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ExceptionCauseRetryTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}

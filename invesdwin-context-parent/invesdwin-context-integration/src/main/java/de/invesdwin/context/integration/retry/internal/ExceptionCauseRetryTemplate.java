package de.invesdwin.context.integration.retry.internal;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

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
        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(new Duration(1, FTimeUnit.SECONDS).longValue(FTimeUnit.MILLISECONDS));
        backOffPolicy.setMaxInterval(new Duration(15, FTimeUnit.MINUTES).longValue(FTimeUnit.MILLISECONDS));
        setBackOffPolicy(backOffPolicy);
        setRetryPolicy(ExceptionCauseRetryPolicy.INSTANCE);
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

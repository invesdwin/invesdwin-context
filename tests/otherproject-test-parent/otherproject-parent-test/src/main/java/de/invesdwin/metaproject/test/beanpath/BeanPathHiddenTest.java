package de.invesdwin.metaproject.test.beanpath;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.beanpath.annotation.BeanPathRedirect;
import de.invesdwin.norva.beanpath.annotation.Hidden;

// CS:OFF VisibilityModifier
@NotThreadSafe
public class BeanPathHiddenTest extends ABeanPathRoot {

    @Hidden
    public String hiddenField;
    public String hiddenFieldViaGetter;
    private final Internal internal = null;

    @Hidden
    public String getHiddenFieldViaGetter() {
        return hiddenFieldViaGetter;
    }

    public Internal getInternal() {
        return internal;
    }

    @BeanPathRedirect("internal.internalGetter")
    @Hidden
    public String getHideInterceptor() {
        return null;
    }

    public Void getVoidType() {
        return null;
    }

    @BeanPathRedirect(BeanPathHiddenTestConstants.internal_internalInternal)
    public InterceptedType getInternalIntercepted() {
        return new InterceptedType();
    }

    public static class Internal {
        public InternalInternal internalInternal;
        public String internalField;
        private final String internalGetter = null;

        public String getInternalGetter() {
            return internalGetter;
        }
    }

    public static class InternalInternal {
        public String shouldNotBeVisibleViaInterceptorTypeRedirect;
    }

    public static class InterceptedType {
        public String thisWasIntercepted;
    }

}

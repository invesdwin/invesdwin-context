package de.invesdwin.metaproject.test.beanpath;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.metaproject.test.beanpath.BeanPathTest.SomeEnumOption.Option1Config;
import de.invesdwin.metaproject.test.beanpath.BeanPathTest.SomeEnumOption.Option2Config;

// CS:OFF VisibilityModifier
@NotThreadSafe
public class BeanPathTest extends ABeanPathRoot {

    public enum SomeEnumOption {
        Option1,
        Option2;

        public static class Option1Config {
            public final BeanPathInternal internal = new BeanPathInternal();
            public boolean someChoice1;
        }

        public class Option2Config {
            public boolean someChoice21;
            public Integer someChoice22;
            public int someChoice23;
            public int[] someArrayChoice24;
            public Collection<Integer> someCollectionChoice25;
        }

    }

    public String someProperty;
    public String someInvisibleProperty;
    public String someGetterAndFieldProperty;
    public SomeEnumOption someEnumOption;
    public Option1Config someEnumOption_Option1;
    public Option2Config someEnumOption_Option2;
    private final String someGetterProperty = null;
    private final BeanPathInternal internalGetter = new BeanPathInternal();

    public BeanPathInternal getInternalGetter() {
        return internalGetter;
    }

    public static class BeanPathInternal {
        public String someInternalProperty;
        public final BeanPathInternalInternal internal = new BeanPathInternalInternal();
    }

    public static class BeanPathInternalInternal {
        public String someInternalInternalProperty;
    }

    public String getSomeGetterProperty() {
        return someGetterProperty;
    }

    public String getSomeGetterAndFieldProperty() {
        return someGetterAndFieldProperty;
    }

}

package de.invesdwin.context.beans.init.platform.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.classpath.FastClassPathScanner;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.LocalFastSerializingSerde;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

@NotThreadSafe
public class RegisterTypesForSerializationConfigurer {

    public static final Class<ISerializableValueObject> SERIALIZABLE_INTERFACE = ISerializableValueObject.class;

    public void registerTypesForSerialization() {
        final LocalFastSerializingSerde<Serializable> serde = LocalFastSerializingSerde.get();
        final List<Class<?>> classesToRegister = scanSerializableClassesToRegister();
        //sort them so they always get the same index in registration
        classesToRegister.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(final Class<?> o1, final Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        serde.setClassRegistry(classesToRegister);
    }

    protected List<Class<?>> scanSerializableClassesToRegister() {
        /*
         * performance optimization see: https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
         */
        final ScanResult scanner = FastClassPathScanner.getScanResult();
        final List<Class<?>> classesToRegister = new ArrayList<Class<?>>();
        for (final ClassInfo ci : scanner.getClassesImplementing(SERIALIZABLE_INTERFACE.getName())) {
            final Class<?> clazz = Reflections.classForName(ci.getName());
            classesToRegister.add(clazz);
        }
        return classesToRegister;
    }

}

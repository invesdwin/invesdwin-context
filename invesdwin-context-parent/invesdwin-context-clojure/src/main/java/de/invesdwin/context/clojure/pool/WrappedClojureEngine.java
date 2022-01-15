package de.invesdwin.context.clojure.pool;

import java.io.Closeable;
import java.io.StringReader;

import javax.annotation.concurrent.NotThreadSafe;

import clojure.lang.Compiler;
import clojure.lang.LineNumberingPushbackReader;
import clojure.lang.LispReader;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public final class WrappedClojureEngine implements Closeable {

    private static final FastThreadLocal<WrappedClojureEngine> INSTANCE = new FastThreadLocal<WrappedClojureEngine>() {
        @Override
        protected WrappedClojureEngine initialValue() throws Exception {
            return new WrappedClojureEngine();
        }
    };

    private final ClojureBindings binding;

    private WrappedClojureEngine() {
        binding = new ClojureBindings();
        binding.put("clojure.core.*file*", "/script");
    }

    public ClojureBindings getBinding() {
        return binding;
    }

    public Object eval(final String expression) {
        final LineNumberingPushbackReader reader = new LineNumberingPushbackReader(new StringReader(expression));
        Object finalResult = null;
        while (true) {
            final Object form = LispReader.read(reader, false, this, false);
            if (form == this) {
                break;
            }
            finalResult = Compiler.eval(form);
        }
        return finalResult;
    }

    public void reset() {
        binding.clear();
    }

    @Override
    public void close() {
        reset();
    }

    public void put(final String variable, final Object value) {
        binding.put(variable, value);
    }

    public Object get(final String variable) {
        return eval(variable);
    }

    public void remove(final String variable) {
        binding.remove(variable);
    }

    public boolean contains(final String variable) {
        return binding.containsKey(variable);
    }

    public static WrappedClojureEngine getInstance() {
        return INSTANCE.get();
    }

}

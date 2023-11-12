package de.invesdwin.context.clojure.pool;

import java.io.Closeable;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

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
    private static final Object EOF = new Object();

    private final LoadingCache<String, List<Object>> scriptCache;

    private final ClojureBindings binding;

    private WrappedClojureEngine() {
        scriptCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .softValues()
                .<String, List<Object>> build((key) -> compile(new StringReader(key)));
        binding = new ClojureBindings();
        binding.put("clojure.core.*file*", "/" + binding.getNamespace());
        binding.put("binding", binding);
    }

    public ClojureBindings getBinding() {
        return binding;
    }

    public Object eval(final String expression) {
        return evalCompiling(expression);
    }

    public Object evalParsing(final String expression) {
        return evalParsing(new StringReader(expression));
    }

    public static Object evalParsing(final Reader reader) {
        final LineNumberingPushbackReader pushbackReader = new LineNumberingPushbackReader(reader);
        Object finalResult = null;
        while (true) {
            final Object form = LispReader.read(pushbackReader, false, EOF, false);
            if (form == EOF) {
                break;
            }
            finalResult = Compiler.eval(form);
        }
        return finalResult;
    }

    public void resetScriptCache() {
        scriptCache.asMap().clear();
    }

    public Object evalCompiling(final String expression) {
        final List<Object> compiled = scriptCache.get(expression);
        return evalCompiled(compiled);
    }

    public static Object evalCompiled(final List<Object> compiled) {
        final int lastIndex = compiled.size() - 1;
        for (int i = 0; i < lastIndex; i++) {
            Compiler.eval(compiled.get(i));
        }
        return Compiler.eval(compiled.get(lastIndex));
    }

    public static List<Object> compile(final Reader reader) {
        final LineNumberingPushbackReader pushbackReader = new LineNumberingPushbackReader(reader);
        final List<Object> compiled = new ArrayList<>();
        while (true) {
            final Object form = LispReader.read(pushbackReader, false, EOF, false);
            if (form == EOF) {
                break;
            }
            compiled.add(form);
        }
        return compiled;
    }

    public void reset() {
        binding.clear();
        binding.put("binding", binding);
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

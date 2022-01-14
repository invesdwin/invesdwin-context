package de.invesdwin.context.clojure.pool;

import java.io.Closeable;
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

@NotThreadSafe
public class WrappedClojureEngine implements Closeable {

    private final LoadingCache<String, List<Object>> scriptCache;

    public WrappedClojureEngine() {
        scriptCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .softValues()
                .<String, List<Object>> build((key) -> parse(key));
        ClojureBindings.INSTANCE.put("clojure.core.*file*", "/clojure-dynamic-script");
    }

    public ClojureBindings getBinding() {
        return ClojureBindings.INSTANCE;
    }

    public Object eval(final String expression) {
        return evalCompiling(expression);
    }

    public Object evalParsing(final String expression) {
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

    public Object evalCompiling(final String expression) {
        final List<Object> parsed = scriptCache.get(expression);
        for (int i = 0; i < parsed.size() - 2; i++) {
            Compiler.eval(parsed.get(i));
        }
        return Compiler.eval(parsed.get(parsed.size() - 1));
    }

    private List<Object> parse(final String expression) {
        final LineNumberingPushbackReader reader = new LineNumberingPushbackReader(new StringReader(expression));
        final List<Object> parsed = new ArrayList<>();
        while (true) {
            final Object form = LispReader.read(reader, false, this, false);
            if (form == this) {
                break;
            }
            parsed.add(form);
        }
        return parsed;
    }

    public void reset() {
        //https://stackoverflow.com/questions/3636364/can-i-clean-the-repl
        //        eval("(map #(ns-unmap *ns* %) (keys (ns-interns *ns*)))");
        eval("(remove-ns 'user)");
        eval("(create-ns 'user)");
    }

    @Override
    public void close() {
        reset();
    }

    public void put(final String variable, final Object value) {
        ClojureBindings.INSTANCE.put(variable, value);
    }

    public Object get(final String variable) {
        return eval(variable);
    }

    public void remove(final String variable) {
        ClojureBindings.INSTANCE.remove(variable);
    }

    public boolean contains(final String variable) {
        return ClojureBindings.INSTANCE.containsKey(variable);
    }

}

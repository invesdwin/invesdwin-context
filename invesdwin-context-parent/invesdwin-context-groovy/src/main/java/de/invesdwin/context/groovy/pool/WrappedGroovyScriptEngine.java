package de.invesdwin.context.groovy.pool;

import java.io.Closeable;
import java.util.LinkedHashMap;

import javax.annotation.concurrent.NotThreadSafe;

import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

@NotThreadSafe
public class WrappedGroovyScriptEngine implements Closeable {

    private static final GroovyScriptEngineFactory FACTORY = new GroovyScriptEngineFactory();

    private final GroovyScriptEngine engine;
    private final Binding binding;

    public WrappedGroovyScriptEngine() {
        this.engine = (GroovyScriptEngine) FACTORY.getScriptEngine();
        this.binding = new Binding(new LinkedHashMap<>());
    }

    public GroovyScriptEngine getEngine() {
        return engine;
    }

    public Binding getBinding() {
        return binding;
    }

    public void eval(final String expression) {
        System.out.println("§TODO");
    }

    public void reset() {
        binding.getVariables().clear();
    }

    @Override
    public void close() {
        reset();
    }

    public void put(final String variable, final Object value) {
        binding.setVariable(variable, value);
    }

    public Object get(final String variable) {
        return binding.getVariable(variable);
    }

    public void remove(final String variable) {
        binding.removeVariable(variable);
    }

    public boolean contains(final String variable) {
        return binding.hasVariable(variable);
    }

}

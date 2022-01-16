package de.invesdwin.context.jshell.pool;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@NotThreadSafe
public class WrappedBeanshellScriptEngine implements Closeable {

    private final ScriptEngine engine;
    private final Invocable invocable;
    private final Bindings binding;

    public WrappedBeanshellScriptEngine() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("beanshell");
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (engine instanceof Invocable) {
            invocable = (Invocable) engine;
        } else {
            invocable = null;
        }
        reset();
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public Bindings getBinding() {
        return binding;
    }

    public Invocable getInvocable() {
        return invocable;
    }

    public Object eval(final String expression) {
        try {
            return engine.eval(expression);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
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

}

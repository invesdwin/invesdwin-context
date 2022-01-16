package de.invesdwin.context.beanshell.pool;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@NotThreadSafe
public class WrappedBeanshellScriptEngine implements IBeanshellEngine {

    private final ScriptEngine engine;
    private final Compilable compilable;
    private final Invocable invocable;
    private final Bindings binding;

    public WrappedBeanshellScriptEngine() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("beanshell");
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (engine instanceof Compilable) {
            compilable = (Compilable) engine;
        } else {
            compilable = null;
        }
        if (engine instanceof Invocable) {
            invocable = (Invocable) engine;
        } else {
            invocable = null;
        }

    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public Bindings getBinding() {
        return binding;
    }

    public Compilable getCompilable() {
        return compilable;
    }

    public Invocable getInvocable() {
        return invocable;
    }

    @Override
    public Object eval(final String expression) {
        try {
            return engine.eval(expression);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reset() {
        binding.clear();
    }

    @Override
    public void close() {
        reset();
    }

    @Override
    public void put(final String variable, final Object value) {
        binding.put(variable, value);
    }

    @Override
    public Object get(final String variable) {
        return eval(variable);
    }

    @Override
    public void remove(final String variable) {
        binding.remove(variable);
    }

    @Override
    public boolean contains(final String variable) {
        return binding.containsKey(variable);
    }

}

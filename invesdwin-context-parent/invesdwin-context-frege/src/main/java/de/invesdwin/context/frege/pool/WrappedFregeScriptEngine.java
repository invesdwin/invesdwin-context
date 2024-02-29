package de.invesdwin.context.frege.pool;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class WrappedFregeScriptEngine implements Closeable {

    private final ScriptEngine engine;
    private final Invocable invocable;
    private final Bindings binding;

    public WrappedFregeScriptEngine() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("frege");
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        //        this.binding.put("binding", binding);
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

    public Object eval(final String expression, final Bindings bindings) {
        try {
            return engine.eval(expression, bindings);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        binding.clear();
        //        binding.put("binding", binding);
    }

    @Override
    public void close() {
        reset();
    }

    public void put(final String variable, final Object value) {
        if (value == null) {
            remove(variable);
        } else {
            binding.put(variable + " :: " + getFregeType(value), value);
        }
    }

    private String getFregeType(final Object value) {
        if (value instanceof String) {
            return "String";
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
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

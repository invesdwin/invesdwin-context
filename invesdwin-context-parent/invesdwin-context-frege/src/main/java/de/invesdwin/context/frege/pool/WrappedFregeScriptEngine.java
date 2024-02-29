package de.invesdwin.context.frege.pool;

import java.io.Closeable;
import java.math.BigInteger;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.string.Strings;

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
            binding.put(variable + " :: String", Strings.NULL_TEXT);
        } else {
            final String type = getFregeType(value.getClass());
            binding.put(variable + " :: " + type, value);
        }
    }

    private String getFregeType(final Class<?> type) {
        if (type.isArray()) {
            final Class<?> componentType = type.getComponentType();
            if (componentType.isArray()) {
                final String fregeType = getFregeType(componentType.getComponentType());
                return "JArray (" + fregeType + ", " + fregeType + ")";
            } else {
                return "JArray " + getFregeType(componentType);
            }
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return "String";
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return "Bool";
        } else if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type)) {
            return "Char";
        } else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return "Byte";
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return "Int";
        } else if (BigInteger.class.isAssignableFrom(type)) {
            return "Integer";
        } else {
            throw UnknownArgumentException.newInstance(Class.class, type);
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

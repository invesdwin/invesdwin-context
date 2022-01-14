package de.invesdwin.context.jruby.pool;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

@NotThreadSafe
public class WrappedJrubyScriptEngine implements Closeable {

    private final LoadingCache<String, CompiledScript> scriptCache;

    private final ScriptEngine engine;
    private final Compilable compilable;
    private final Invocable invocable;
    private final Bindings binding;
    private final Function<String, Object> evalF;

    public WrappedJrubyScriptEngine() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("jruby");
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (engine instanceof Compilable) {
            compilable = (Compilable) engine;
            scriptCache = Caffeine.newBuilder()
                    .maximumSize(100)
                    .expireAfterAccess(1, TimeUnit.MINUTES)
                    .softValues()
                    .<String, CompiledScript> build((key) -> compilable.compile(key));
            evalF = (expression) -> evalCompiling(expression);
        } else {
            compilable = null;
            scriptCache = null;
            evalF = (expression) -> evalParsing(expression);
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

    public Object eval(final String expression) {
        return evalF.apply(expression);
    }

    private Object evalCompiling(final String expression) {
        final CompiledScript parsed = scriptCache.get(expression);
        try {
            return parsed.eval();
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private Object evalParsing(final String expression) {
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

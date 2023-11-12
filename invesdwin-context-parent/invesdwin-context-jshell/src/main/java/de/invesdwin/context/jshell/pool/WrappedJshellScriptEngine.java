package de.invesdwin.context.jshell.pool;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
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
public class WrappedJshellScriptEngine implements Closeable {

    private final LoadingCache<String, CompiledScript> scriptCache;

    private final ScriptEngine engine;
    private final Compilable compilable;
    private final Invocable invocable;
    private final Bindings binding;
    private final Function<String, Object> evalF;
    private final BiFunction<String, Bindings, Object> evalBindingsF;

    public WrappedJshellScriptEngine() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("jshell");
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        this.binding.put("binding", binding);
        if (engine instanceof Compilable) {
            compilable = (Compilable) engine;
            scriptCache = Caffeine.newBuilder()
                    .maximumSize(100)
                    .expireAfterAccess(1, TimeUnit.MINUTES)
                    .softValues()
                    .<String, CompiledScript> build((key) -> compilable.compile(key));
            evalF = (expression) -> evalCompiling(expression);
            evalBindingsF = (expression, bindings) -> evalBindingsCompiling(expression, bindings);
        } else {
            compilable = null;
            scriptCache = null;
            evalF = (expression) -> evalParsing(expression);
            evalBindingsF = (expression, bindings) -> evalBindingsParsing(expression, bindings);
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

    public Object eval(final String expression, final Bindings bindings) {
        return evalBindingsF.apply(expression, bindings);
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

    private Object evalBindingsCompiling(final String expression, final Bindings bindings) {
        final CompiledScript parsed = scriptCache.get(expression);
        try {
            return parsed.eval(bindings);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private Object evalBindingsParsing(final String expression, final Bindings bindings) {
        try {
            return engine.eval(expression, bindings);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
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

}

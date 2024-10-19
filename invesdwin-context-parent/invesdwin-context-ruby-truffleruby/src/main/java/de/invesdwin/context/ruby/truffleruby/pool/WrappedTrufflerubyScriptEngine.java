package de.invesdwin.context.ruby.truffleruby.pool;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.context.graalvm.jsr223.PolyglotScriptEngine;
import de.invesdwin.context.ruby.truffleruby.jsr223.TrufflerubyScriptEngineFactory;

@NotThreadSafe
public class WrappedTrufflerubyScriptEngine implements Closeable {

    private final LoadingCache<String, CompiledScript> scriptCache;

    private final PolyglotScriptEngine engine;
    private final Compilable compilable;
    private final Invocable invocable;
    private final Bindings binding;
    private final Function<String, Object> evalF;
    private final String origGlobalVariables;

    public WrappedTrufflerubyScriptEngine() {
        this.engine = TrufflerubyScriptEngineFactory.INSTANCE.getScriptEngine();
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        this.binding.put("$binding", binding);
        if (engine instanceof Compilable) {
            compilable = engine;
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
            invocable = engine;
        } else {
            invocable = null;
        }
        this.origGlobalVariables = newOrigGlobalVariables();
    }

    @SuppressWarnings("unchecked")
    private String newOrigGlobalVariables() {
        final StringBuilder sb = new StringBuilder("[");
        final List<String> vars = (List<String>) eval("global_variables");
        for (int i = 0; i < vars.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(":");
            sb.append(vars.get(i));
        }
        sb.append("]");
        return sb.toString();
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
        //        binding.clear();
        this.binding.put("$binding", binding);
        if (scriptCache != null) {
            //we have to reset the script cache or ruby throws weird AssertionErrors
            scriptCache.asMap().clear();
        }
        //https://stackoverflow.com/a/72504691
        eval("(local_variables + global_variables - " + origGlobalVariables + ").each { |e| eval(\"#{e} = nil\") }");
    }

    @Override
    public void close() {
        reset();
        engine.close();
    }

    public void put(final String variable, final Object value) {
        binding.put(variable, value);
    }

    public Object get(final String variable) {
        return eval(variable);
    }

    public void remove(final String variable) {
        put(variable, null);
        //UnsupportedOperationException
        //        binding.remove(variable);
    }

    public boolean contains(final String variable) {
        return binding.get(variable) != null;
    }

}

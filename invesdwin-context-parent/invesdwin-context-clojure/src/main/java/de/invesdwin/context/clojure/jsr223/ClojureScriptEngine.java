package de.invesdwin.context.clojure.jsr223;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import clojure.lang.Var;
import de.invesdwin.context.clojure.pool.ClojureBindings;
import de.invesdwin.context.clojure.pool.WrappedClojureEngine;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public final class ClojureScriptEngine extends AbstractScriptEngine implements Compilable {

    private static final FastThreadLocal<ClojureScriptEngine> INSTANCE = new FastThreadLocal<ClojureScriptEngine>() {
        @Override
        protected ClojureScriptEngine initialValue() throws Exception {
            return new ClojureScriptEngine();
        }
    };

    private final ClojureBindings engineScopeBinding;
    private final String defaultFileName;

    private ClojureScriptEngine() {
        final ClojureScriptContext context = new ClojureScriptContext();
        setContext(context);
        this.engineScopeBinding = (ClojureBindings) context.getBindings(ScriptContext.ENGINE_SCOPE);
        this.defaultFileName = "/" + engineScopeBinding.getNamespace();
    }

    @Override
    public Object eval(final String script) throws ScriptException {
        final ScriptContext context = getContext();
        return eval(script, context);
    }

    @Override
    public Object eval(final Reader reader) throws ScriptException {
        final ScriptContext context = getContext();
        return eval(reader, context);
    }

    @Override
    public Object eval(final String script, final ScriptContext context) throws ScriptException {
        try {
            return eval(new StringReader(script), context);
        } catch (final Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(final Reader reader, final ScriptContext context) throws ScriptException {
        setup(context);
        try {
            final Object filename = get(ScriptEngine.FILENAME);
            if (filename == null || filename instanceof Var.Unbound) {
                // make up a fake filename, to make clojure happy
                put("clojure.core.*file*", defaultFileName);
            } else {
                // use the real filename
                put("clojure.core.*file*", filename);
            }
            return WrappedClojureEngine.evalParsing(reader);
        } catch (final Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public ClojureCompiledScript compile(final String script) throws ScriptException {
        return compile(new StringReader(script));
    }

    @Override
    public ClojureCompiledScript compile(final Reader script) throws ScriptException {
        return new ClojureCompiledScript(this, script);
    }

    protected void setup(final ScriptContext context) {
        final Reader reader = context.getReader();
        if (reader != null) {
            engineScopeBinding.put("clojure.core.*in*", reader);
        }
        final Writer writer = context.getWriter();
        if (writer != null) {
            engineScopeBinding.put("clojure.core.*out*", writer);
        }
        final Writer errorWriter = context.getErrorWriter();
        if (errorWriter != null) {
            engineScopeBinding.put("clojure.core.*err*", errorWriter);
        }
    }

    @Override
    public Bindings createBindings() {
        return engineScopeBinding;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return ClojureScriptEngineFactory.getInstance();
    }

    public static ClojureScriptEngine getInstance() {
        return INSTANCE.get();
    }

}
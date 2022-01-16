package de.invesdwin.context.clojure.jsr223;

import java.io.Reader;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import de.invesdwin.context.clojure.pool.WrappedClojureEngine;

@NotThreadSafe
public class ClojureCompiledScript extends CompiledScript {

    private final ClojureScriptEngine engine;
    private final List<Object> compiled;

    public ClojureCompiledScript(final ClojureScriptEngine engine, final Reader reader) {
        this.engine = engine;
        this.compiled = WrappedClojureEngine.compile(reader);
    }

    @Override
    public Object eval(final ScriptContext context) throws ScriptException {
        return WrappedClojureEngine.evalCompiled(compiled);
    }

    @Override
    public ClojureScriptEngine getEngine() {
        return engine;
    }

}

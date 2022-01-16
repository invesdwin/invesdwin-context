package de.invesdwin.context.clojure.jsr223;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.ScriptContext;

import de.invesdwin.context.clojure.pool.ClojureBindings;
import de.invesdwin.context.clojure.pool.WrappedClojureEngine;

@NotThreadSafe
public class ClojureScriptContext implements ScriptContext {

    private static final List<Integer> SCOPES = Arrays.asList(ScriptContext.ENGINE_SCOPE, ScriptContext.GLOBAL_SCOPE);

    private ClojureBindings engineScope;
    private ClojureBindings globalScope;
    private Reader reader;
    private Writer writer;
    private Writer errorWriter;

    public ClojureScriptContext() {
        this.engineScope = WrappedClojureEngine.getInstance().getBinding();
        this.globalScope = new ClojureBindings(ClojureBindings.CORE_NS, false);
        this.reader = new InputStreamReader(System.in);
        this.writer = new PrintWriter(System.out, true);
        this.errorWriter = new PrintWriter(System.err, true);
    }

    @Override
    public void setBindings(final Bindings bindings, final int scope)
            throws IllegalArgumentException, NullPointerException {
        ClojureBindings clojureBindings = null;
        if (bindings != null) {
            if (bindings instanceof ClojureBindings) {
                clojureBindings = (ClojureBindings) bindings;
            } else {
                clojureBindings = new ClojureBindings(engineScope.getNamespace(), false);
                clojureBindings.putAll(bindings);
            }
        }

        switch (scope) {
        case ScriptContext.ENGINE_SCOPE:
            if (clojureBindings != null) {
                this.engineScope = clojureBindings;
            }
            break;
        case ScriptContext.GLOBAL_SCOPE:
            this.globalScope = clojureBindings;
            break;
        default:
            throw new IllegalArgumentException("Invalid scope declaration.");
        }
    }

    @Override
    public Bindings getBindings(final int scope) throws IllegalArgumentException {
        switch (scope) {
        case ScriptContext.ENGINE_SCOPE:
            return this.engineScope;
        case ScriptContext.GLOBAL_SCOPE:
            return this.globalScope;
        default:
            throw new IllegalArgumentException("Invalid scope declaration.");
        }
    }

    @Override
    public void setAttribute(final String paramString, final Object paramObject, final int scope)
            throws IllegalArgumentException, NullPointerException {
        if (paramString == null) {
            throw new NullPointerException("Name is null.");
        }
        if (paramString.isEmpty()) {
            throw new IllegalArgumentException("Name is empty.");
        }
        switch (scope) {
        case ScriptContext.ENGINE_SCOPE:
            this.engineScope.put(paramString, paramObject);
            break;
        case ScriptContext.GLOBAL_SCOPE:
            this.globalScope.put(paramString, paramObject);
            break;
        default:
            throw new IllegalArgumentException("Invalid scope declaration.");
        }
    }

    @Override
    public Object getAttribute(final String paramString, final int scope)
            throws IllegalArgumentException, NullPointerException {
        if (paramString == null) {
            throw new NullPointerException("Name is null.");
        }
        if (paramString.isEmpty()) {
            throw new IllegalArgumentException("Name is empty.");
        }
        switch (scope) {
        case ScriptContext.ENGINE_SCOPE:
            return this.engineScope.get(paramString);
        case ScriptContext.GLOBAL_SCOPE:
            return this.globalScope.get(paramString);
        default:
            throw new IllegalArgumentException("Invalid scope declaration.");
        }
    }

    @Override
    public Object removeAttribute(final String paramString, final int scope) {
        if (paramString == null) {
            throw new NullPointerException("Name is null.");
        }
        if (paramString.isEmpty()) {
            throw new IllegalArgumentException("Name is empty.");
        }
        switch (scope) {
        case ScriptContext.ENGINE_SCOPE:
            return this.engineScope.remove(paramString);
        case ScriptContext.GLOBAL_SCOPE:
            return this.globalScope.remove(paramString);
        default:
            throw new IllegalArgumentException("Invalid scope declaration.");
        }
    }

    @Override
    public Object getAttribute(final String paramString) throws IllegalArgumentException, NullPointerException {
        Object object = getAttribute(paramString, 100);
        if (object == null) {
            object = getAttribute(paramString, 200);
        }
        return object;
    }

    @Override
    public int getAttributesScope(final String paramString) throws IllegalArgumentException, NullPointerException {
        if (paramString == null) {
            throw new NullPointerException("Name is null.");
        }
        if (paramString.isEmpty()) {
            throw new IllegalArgumentException("Name is empty.");
        }
        if (this.engineScope.containsKey(paramString)) {
            return 100;
        }
        if (this.globalScope.containsKey(paramString)) {
            return 200;
        }
        return -1;
    }

    @Override
    public Writer getWriter() {
        return this.writer;
    }

    @Override
    public Writer getErrorWriter() {
        return this.errorWriter;
    }

    @Override
    public void setWriter(final Writer paramWriter) {
        this.writer = paramWriter;
    }

    @Override
    public void setErrorWriter(final Writer paramWriter) {
        this.errorWriter = paramWriter;
    }

    @Override
    public Reader getReader() {
        return this.reader;
    }

    @Override
    public void setReader(final Reader paramReader) {
        this.reader = paramReader;
    }

    @Override
    public List<Integer> getScopes() {
        return SCOPES;
    }
}
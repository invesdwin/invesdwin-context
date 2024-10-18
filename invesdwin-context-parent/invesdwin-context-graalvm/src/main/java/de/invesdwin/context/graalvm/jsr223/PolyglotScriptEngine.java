package de.invesdwin.context.graalvm.jsr223;

import java.io.IOException;
import java.io.Reader;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

@NotThreadSafe
public class PolyglotScriptEngine implements ScriptEngine, Compilable, Invocable, AutoCloseable {
    private final PolyglotScriptEngineFactory factory;
    private final PolyglotContext defaultContext;

    public PolyglotScriptEngine(final PolyglotScriptEngineFactory factory) {
        this.factory = factory;
        this.defaultContext = new PolyglotContext(factory);
    }

    @Override
    public void close() {
        defaultContext.getContext().close();
    }

    @Override
    public CompiledScript compile(final String script) throws ScriptException {
        final Source src = Source.create(factory.getLanguageId(), script);
        try {
            defaultContext.getContext().parse(src); // only for the side-effect of validating the source
        } catch (final PolyglotException e) {
            throw new ScriptException(e);
        }
        return new PolyglotCompiledScript(src, this);
    }

    @Override
    public CompiledScript compile(final Reader script) throws ScriptException {
        final Source src;
        try {
            src = Source.newBuilder(factory.getLanguageId(), script, "sourcefromreader").build();
            defaultContext.getContext().parse(src); // only for the side-effect of validating the source
        } catch (PolyglotException | IOException e) {
            throw new ScriptException(e);
        }
        return new PolyglotCompiledScript(src, this);
    }

    @Override
    public Object eval(final String script, final ScriptContext context) throws ScriptException {
        if (context instanceof PolyglotContext) {
            final PolyglotContext c = (PolyglotContext) context;
            try {
                return c.getContext().eval(factory.getLanguageId(), script).as(Object.class);
            } catch (final PolyglotException e) {
                throw new ScriptException(e);
            }
        } else {
            throw new ClassCastException("invalid context");
        }
    }

    @Override
    public Object eval(final Reader reader, final ScriptContext context) throws ScriptException {
        final Source src;
        try {
            src = Source.newBuilder(factory.getLanguageId(), reader, "sourcefromreader").build();
        } catch (final IOException e) {
            throw new ScriptException(e);
        }
        if (context instanceof PolyglotContext) {
            final PolyglotContext c = (PolyglotContext) context;
            try {
                return c.getContext().eval(src).as(Object.class);
            } catch (final PolyglotException e) {
                throw new ScriptException(e);
            }
        } else {
            throw new ScriptException("invalid context");
        }
    }

    @Override
    public Object eval(final String script) throws ScriptException {
        return eval(script, defaultContext);
    }

    @Override
    public Object eval(final Reader reader) throws ScriptException {
        return eval(reader, defaultContext);
    }

    @Override
    public Object eval(final String script, final Bindings n) throws ScriptException {
        throw new UnsupportedOperationException("Bindings for Polyglot language cannot be created explicitly");
    }

    @Override
    public Object eval(final Reader reader, final Bindings n) throws ScriptException {
        throw new UnsupportedOperationException("Bindings for Polyglot language cannot be created explicitly");
    }

    @Override
    public void put(final String key, final Object value) {
        defaultContext.getBindings(ScriptContext.ENGINE_SCOPE).put(key, value);
    }

    @Override
    public Object get(final String key) {
        return defaultContext.getBindings(ScriptContext.ENGINE_SCOPE).get(key);
    }

    @Override
    public Bindings getBindings(final int scope) {
        return defaultContext.getBindings(scope);
    }

    @Override
    public void setBindings(final Bindings bindings, final int scope) {
        defaultContext.setBindings(bindings, scope);
    }

    @Override
    public Bindings createBindings() {
        throw new UnsupportedOperationException("Bindings for Polyglot language cannot be created explicitly");
    }

    @Override
    public ScriptContext getContext() {
        return defaultContext;
    }

    @Override
    public void setContext(final ScriptContext context) {
        throw new UnsupportedOperationException("The context of a Polyglot ScriptEngine cannot be modified.");
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    @Override
    public Object invokeMethod(final Object thiz, final String name, final Object... args)
            throws ScriptException, NoSuchMethodException {
        try {
            final Value receiver = defaultContext.getContext().asValue(thiz);
            if (receiver.canInvokeMember(name)) {
                return receiver.invokeMember(name, args).as(Object.class);
            } else {
                throw new NoSuchMethodException(name);
            }
        } catch (final PolyglotException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object invokeFunction(final String name, final Object... args)
            throws ScriptException, NoSuchMethodException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getInterface(final Class<T> clasz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getInterface(final Object thiz, final Class<T> interfaceClass) {
        return defaultContext.getContext().asValue(thiz).as(interfaceClass);
    }
}
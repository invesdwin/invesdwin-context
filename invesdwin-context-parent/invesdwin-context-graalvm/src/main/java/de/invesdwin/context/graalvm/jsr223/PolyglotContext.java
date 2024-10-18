package de.invesdwin.context.graalvm.jsr223;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.ScriptContext;

import org.graalvm.polyglot.Context;

@NotThreadSafe
public class PolyglotContext implements ScriptContext {
    private Context context;
    @SuppressWarnings("unused")
    private final PolyglotScriptEngineFactory factory;
    private final PolyglotReader in;
    private final PolyglotWriter out;
    private final PolyglotWriter err;
    private Bindings globalBindings;

    public PolyglotContext(final PolyglotScriptEngineFactory factory) {
        this.factory = factory;
        this.in = new PolyglotReader(new InputStreamReader(System.in));
        this.out = new PolyglotWriter(new OutputStreamWriter(System.out));
        this.err = new PolyglotWriter(new OutputStreamWriter(System.err));
    }

    public Context getContext() {
        if (context == null) {
            final Context.Builder builder = factory.customizeContextBuilder(Context.newBuilder(factory.getLanguageId()))
                    .in(this.in)
                    .out(this.out)
                    .err(this.err);
            final Bindings globalBindings = getBindings(ScriptContext.GLOBAL_SCOPE);
            if (globalBindings != null) {
                for (final Entry<String, Object> entry : globalBindings.entrySet()) {
                    final Object value = entry.getValue();
                    if (value instanceof String) {
                        builder.option(entry.getKey(), (String) value);
                    }
                }
            }
            context = builder.build();
        }
        return context;
    }

    @Override
    public void setBindings(final Bindings bindings, final int scope) {
        if (scope == ScriptContext.GLOBAL_SCOPE) {
            if (context == null) {
                globalBindings = bindings;
            } else {
                throw new UnsupportedOperationException(
                        "Global bindings for Polyglot language can only be set before the context is initialized.");
            }
        } else {
            throw new UnsupportedOperationException("Bindings objects for Polyglot language is final.");
        }
    }

    @Override
    public Bindings getBindings(final int scope) {
        if (scope == ScriptContext.ENGINE_SCOPE) {
            return new PolyglotBindings(getContext().getBindings(factory.getLanguageId()));
        } else if (scope == ScriptContext.GLOBAL_SCOPE) {
            return globalBindings;
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(final String name, final Object value, final int scope) {
        if (scope == ScriptContext.ENGINE_SCOPE) {
            getBindings(scope).put(name, value);
        } else if (scope == ScriptContext.GLOBAL_SCOPE) {
            if (context == null) {
                globalBindings.put(name, value);
            } else {
                throw new IllegalStateException("Cannot modify global bindings after context creation.");
            }
        }
    }

    @Override
    public Object getAttribute(final String name, final int scope) {
        if (scope == ScriptContext.ENGINE_SCOPE) {
            return getBindings(scope).get(name);
        } else if (scope == ScriptContext.GLOBAL_SCOPE) {
            return globalBindings.get(name);
        }
        return null;
    }

    @Override
    public Object removeAttribute(final String name, final int scope) {
        final Object prev = getAttribute(name, scope);
        if (prev != null) {
            if (scope == ScriptContext.ENGINE_SCOPE) {
                getBindings(scope).remove(name);
            } else if (scope == ScriptContext.GLOBAL_SCOPE) {
                if (context == null) {
                    globalBindings.remove(name);
                } else {
                    throw new IllegalStateException("Cannot modify global bindings after context creation.");
                }
            }
        }
        return prev;
    }

    @Override
    public Object getAttribute(final String name) {
        return getAttribute(name, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public int getAttributesScope(final String name) {
        if (getAttribute(name, ScriptContext.ENGINE_SCOPE) != null) {
            return ScriptContext.ENGINE_SCOPE;
        } else if (getAttribute(name, ScriptContext.GLOBAL_SCOPE) != null) {
            return ScriptContext.GLOBAL_SCOPE;
        }
        return -1;
    }

    @Override
    public Writer getWriter() {
        return this.out.getWriter();
    }

    @Override
    public Writer getErrorWriter() {
        return this.err.getWriter();
    }

    @Override
    public void setWriter(final Writer writer) {
        this.out.setWriter(writer);
    }

    @Override
    public void setErrorWriter(final Writer writer) {
        this.err.setWriter(writer);
    }

    @Override
    public Reader getReader() {
        return this.in.getReader();
    }

    @Override
    public void setReader(final Reader reader) {
        this.in.setReader(reader);
    }

    @Override
    public List<Integer> getScopes() {
        return List.of(ScriptContext.ENGINE_SCOPE, ScriptContext.GLOBAL_SCOPE);
    }

}
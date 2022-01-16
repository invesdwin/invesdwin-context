package de.invesdwin.context.clojure.jsr223;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

@Immutable
public class ClojureScriptEngineFactory implements ScriptEngineFactory {
    private static final ClojureScriptEngineFactory INSTANCE = new ClojureScriptEngineFactory();
    private static final String ENGINE_NAME = "clojure";
    private static final String ENGINE_VERSION = "undefined";
    private static final String LANGUAGE_NAME = "clojure";
    private static final String LANGUAGE_VERSION = "undefined";
    private static final List<String> FILE_EXTENSIONS = new ArrayList<String>() {
        {
            add("clj");
            add("clojure");
        }
    };
    private static final List<String> MIME_TYPES = new ArrayList<String>();
    private static final List<String> NICK_NAMES = new ArrayList<String>();
    private static final Map<String, String> PARAMETERS = new HashMap<String, String>();

    public static ClojureScriptEngineFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getEngineName() {
        return ENGINE_NAME;
    }

    @Override
    public String getEngineVersion() {
        return ENGINE_VERSION;
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }

    @Override
    public String getLanguageVersion() {
        return LANGUAGE_VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return FILE_EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }

    @Override
    public List<String> getNames() {
        return NICK_NAMES;
    }

    @Override
    public String getOutputStatement(final String toDisplay) {
        return "(print \"" + toDisplay + "\")";
    }

    @Override
    public String getProgram(final String... statements) {
        final StringBuilder stringBuilder = new StringBuilder("");

        for (byte b = 0; b < statements.length; b++) {
            stringBuilder.append(statements[b] + "\n");
        }

        return stringBuilder.toString();
    }

    @Override
    public String getMethodCallSyntax(final String obj, final String m, final String... args) {
        final StringBuilder stringBuilder = new StringBuilder("(." + m);
        stringBuilder.append(" " + obj + " ");
        for (byte b = 0; b < args.length; b++) {
            stringBuilder.append(args[b]);
            if (b == args.length - 1) {
                stringBuilder.append(")");
            } else {
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public Object getParameter(final String key) {
        return PARAMETERS.get(key);
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return ClojureScriptEngine.getInstance();
    }
}
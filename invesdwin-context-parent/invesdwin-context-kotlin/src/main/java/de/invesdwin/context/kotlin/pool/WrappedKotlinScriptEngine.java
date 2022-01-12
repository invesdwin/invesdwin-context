package de.invesdwin.context.kotlin.pool;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@NotThreadSafe
public class WrappedKotlinScriptEngine implements Closeable {

    private final ScriptEngine engine;
    private final Compilable compilable;
    private final Invocable invocable;
    private final Bindings binding;

    public WrappedKotlinScriptEngine() {
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("kotlin");
        this.binding = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (engine instanceof Compilable) {
            compilable = (Compilable) engine;
        } else {
            compilable = null;
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
        //we can not use a script cache here since kotlin gets confused with type declarations (nullable/not nullable)
        //        Caused by: javax.script.ScriptException: ERROR Front-end Internal error: Failed to analyze declaration ScriptingHost5cdcdeb8_Line_0
        //        File being compiled: (1,1) in ScriptingHost5cdcdeb8_Line_0.kts
        //        The root cause org.jetbrains.kotlin.resolve.lazy.NoDescriptorForDeclarationException was thrown at: org.jetbrains.kotlin.resolve.lazy.BasicAbsentDescriptorHandler.diagnoseDescriptorNotFound(AbsentDescriptorHandler.kt:18): org.jetbrains.kotlin.util.KotlinFrontEndException: Front-end Internal error: Failed to analyze declaration ScriptingHost5cdcdeb8_Line_0
        //        File being compiled: (1,1) in ScriptingHost5cdcdeb8_Line_0.kts
        //        The root cause org.jetbrains.kotlin.resolve.lazy.NoDescriptorForDeclarationException was thrown at: org.jetbrains.kotlin.resolve.lazy.BasicAbsentDescriptorHandler.diagnoseDescriptorNotFound(AbsentDescriptorHandler.kt:18)
        //            at org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase.asJsr223EvalResult(KotlinJsr223JvmScriptEngineBase.kt:104)
        //            at org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase.compileAndEval(KotlinJsr223JvmScriptEngineBase.kt:63)
        //            at kotlin.script.experimental.jvmhost.jsr223.KotlinJsr223ScriptEngineImpl.compileAndEval(KotlinJsr223ScriptEngineImpl.kt:95)
        //            at org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase.eval(KotlinJsr223JvmScriptEngineBase.kt:31)
        //            at java.scripting/javax.script.AbstractScriptEngine.eval(AbstractScriptEngine.java:264)
        //            at de.invesdwin.context.kotlin.pool.WrappedKotlinScriptEngine.evalParsing(WrappedKotlinScriptEngine.java:87)
        //            ... 77 more
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
        if (value == null) {
            eval("val " + variable + " = null");
        } else {
            binding.put(variable, value);
        }
    }

    public Object get(final String variable) {
        return eval(variable);
    }

    public void remove(final String variable) {
        eval("val " + variable + " = null");
        binding.remove(variable);
    }

    public boolean contains(final String variable) {
        if (binding.containsKey(variable)) {
            return true;
        } else {
            //we can only be sure by also checking eval for exceptions
            try {
                eval(variable);
                return true;
            } catch (final Throwable t) {
                //ScriptException: Unresolved Reference
                return false;
            }
        }
    }

}

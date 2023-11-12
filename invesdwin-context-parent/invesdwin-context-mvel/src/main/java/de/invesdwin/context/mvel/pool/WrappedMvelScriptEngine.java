package de.invesdwin.context.mvel.pool;

import java.io.Closeable;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.jsr223.MvelCompiledScript;
import org.mvel2.jsr223.MvelScriptEngine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

@NotThreadSafe
public class WrappedMvelScriptEngine implements Closeable {

    private final LoadingCache<String, CompiledScript> scriptCache;

    private final boolean strict;
    private final MvelScriptEngine engine;
    private final Compilable compilable;
    private final Invocable invocable;
    private final Bindings binding;
    private final Function<String, Object> evalF;
    private final BiFunction<String, Bindings, Object> evalBindingsF;

    public WrappedMvelScriptEngine(final boolean strict) {
        this.strict = strict;
        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = (MvelScriptEngine) manager.getEngineByName("mvel");
        //MvelScriptEngine is a singleton, make sure to separate the bindings
        this.binding = engine.createBindings();
        this.binding.put("binding", binding);
        if (engine instanceof Compilable) {
            compilable = engine;
            scriptCache = Caffeine.newBuilder()
                    .maximumSize(100)
                    .expireAfterAccess(1, TimeUnit.MINUTES)
                    .softValues()
                    .<String, CompiledScript> build((key) -> {
                        //                        java.util.concurrent.CompletionException: javax.script.ScriptException: java.lang.StringIndexOutOfBoundsException: offset 201, count -1, length 1041
                        //                        at com.github.benmanes.caffeine.cache.LocalLoadingCache.lambda$newMappingFunction$3(LocalLoadingCache.java:190)
                        //                        at com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2688)
                        //                        at java.base/java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1916)
                        //                        at com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2686)
                        //                        at com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2669)
                        //                        at com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:112)
                        //                        at com.github.benmanes.caffeine.cache.LocalLoadingCache.get(LocalLoadingCache.java:58)
                        //                        at de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine.evalCompiling(WrappedMvelScriptEngine.java:90)
                        //                        at de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine.lambda$1(WrappedMvelScriptEngine.java:50)
                        //                        at de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine.eval(WrappedMvelScriptEngine.java:82)
                        //                        at de.invesdwin.context.mvel.ScriptTaskEngineMvel.eval(ScriptTaskEngineMvel.java:27)
                        //                        at de.invesdwin.context.integration.script.IScriptTaskEngine.eval(IScriptTaskEngine.java:46)
                        //                        at de.invesdwin.context.integration.script.IScriptTaskEngine.eval(IScriptTaskEngine.java:28)
                        //                        at de.invesdwin.context.mvel.callback.MvelScriptTaskCallbackContext.init(MvelScriptTaskCallbackContext.java:40)
                        //                        at de.invesdwin.context.mvel.ScriptTaskRunnerMvel.run(ScriptTaskRunnerMvel.java:40)
                        //                        at de.invesdwin.context.mvel.AScriptTaskMvel.run(AScriptTaskMvel.java:12)
                        //                        at de.invesdwin.context.mvel.tests.callback.ParametersAndReturnsTestByte.testByte(ParametersAndReturnsTestByte.java:51)
                        //                        at de.invesdwin.context.mvel.tests.callback.ParametersAndReturnsTests.test(ParametersAndReturnsTests.java:25)
                        //                        at de.invesdwin.context.mvel.tests.callback.ParametersAndReturnsTests$1.run(ParametersAndReturnsTests.java:47)
                        //                        at de.invesdwin.util.concurrent.internal.WrappedRunnable.run(WrappedRunnable.java:47)
                        //                        at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539)
                        //                        at com.google.common.util.concurrent.TrustedListenableFutureTask$TrustedFutureInterruptibleTask.runInterruptibly(TrustedListenableFutureTask.java:131)
                        //                        at com.google.common.util.concurrent.InterruptibleTask.run(InterruptibleTask.java:76)
                        //                        at com.google.common.util.concurrent.TrustedListenableFutureTask.run(TrustedListenableFutureTask.java:82)
                        //                        at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
                        //                        at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
                        //                        at de.invesdwin.util.concurrent.internal.WrappedThreadFactory.lambda$0(WrappedThreadFactory.java:48)
                        //                        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
                        //                        at java.base/java.lang.Thread.run(Thread.java:833)
                        //                    Caused by: javax.script.ScriptException: java.lang.StringIndexOutOfBoundsException: offset 201, count -1, length 1041
                        //                        at org.mvel2.jsr223.MvelScriptEngine.compiledScript(MvelScriptEngine.java:75)
                        //                        at org.mvel2.jsr223.MvelScriptEngine.compile(MvelScriptEngine.java:62)
                        //                        at de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine.lambda$0(WrappedMvelScriptEngine.java:47)
                        //                        at com.github.benmanes.caffeine.cache.LocalLoadingCache.lambda$newMappingFunction$3(LocalLoadingCache.java:183)
                        //                        ... 28 more
                        //                    Caused by: java.lang.StringIndexOutOfBoundsException: offset 201, count -1, length 1041
                        //                        at java.base/java.lang.String.checkBoundsOffCount(String.java:4591)
                        //                        at java.base/java.lang.String.rangeCheck(String.java:304)
                        //                        at java.base/java.lang.String.<init>(String.java:300)
                        //                        at org.mvel2.optimizers.AbstractOptimizer.capture(AbstractOptimizer.java:238)
                        //                        at org.mvel2.compiler.PropertyVerifier.analyze(PropertyVerifier.java:133)
                        //                        at org.mvel2.compiler.ExpressionCompiler.verify(ExpressionCompiler.java:400)
                        //                        at org.mvel2.compiler.ExpressionCompiler._compile(ExpressionCompiler.java:282)
                        //                        at org.mvel2.compiler.ExpressionCompiler.compile(ExpressionCompiler.java:68)
                        //                        at org.mvel2.MVEL.compileExpression(MVEL.java:827)
                        //                        at org.mvel2.MVEL.compileExpression(MVEL.java:836)
                        //                        at org.mvel2.MVEL.compileExpression(MVEL.java:740)
                        //                        at org.mvel2.jsr223.MvelScriptEngine.compiledScript(MvelScriptEngine.java:72)
                        //                        ... 31 more
                        //MVEL compiler is not thread safe
                        synchronized (WrappedMvelScriptEngine.class) {
                            if (strict) {
                                final ParserContext context = new ParserContext();
                                context.setStrictTypeEnforcement(true);
                                final Serializable compiled = MVEL.compileExpression(key, context);
                                return new MvelCompiledScript(engine, compiled);
                            } else {
                                return compilable.compile(key);
                            }
                        }
                    });
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

    public boolean isStrict() {
        return strict;
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
            return parsed.eval(binding);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private Object evalParsing(final String expression) {
        //MVEL compiler is not thread safe
        synchronized (WrappedMvelScriptEngine.class) {
            try {
                return engine.eval(expression, binding);
            } catch (final ScriptException e) {
                throw new RuntimeException(e);
            }
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
        //MVEL compiler is not thread safe
        synchronized (WrappedMvelScriptEngine.class) {
            try {
                return engine.eval(expression, bindings);
            } catch (final ScriptException e) {
                throw new RuntimeException(e);
            }
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

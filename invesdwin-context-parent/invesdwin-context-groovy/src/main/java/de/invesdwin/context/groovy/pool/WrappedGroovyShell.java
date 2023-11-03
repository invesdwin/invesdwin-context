package de.invesdwin.context.groovy.pool;

import java.io.Closeable;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;

@NotThreadSafe
public class WrappedGroovyShell implements Closeable {

    private final LoadingCache<String, Script> scriptCache;

    private final boolean strict;
    private final GroovyShell engine;
    private final Binding binding;
    private final Function<String, Object> getF;

    public WrappedGroovyShell(final boolean strict) {
        this.strict = strict;
        final CompilerConfiguration config = new CompilerConfiguration();
        //we actually want the dynamic language features here normally
        if (strict) {
            config.addCompilationCustomizers(new ASTTransformationCustomizer(CompileStatic.class));
            config.addCompilationCustomizers(new ASTTransformationCustomizer(TypeChecked.class));
        }
        config.setScriptBaseClass(AGroovyScriptBaseClass.class.getName());
        this.binding = new Binding(new LinkedHashMap<>());
        this.engine = new GroovyShell(binding, config);
        scriptCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .softValues()
                .<String, Script> build((key) -> engine.parse(key));

        if (strict) {
            getF = (variable) -> binding.getVariable(variable);
        } else {
            getF = (variable) -> eval(variable);
        }
    }

    public boolean isStrict() {
        return strict;
    }

    public GroovyShell getEngine() {
        return engine;
    }

    public Binding getBinding() {
        return binding;
    }

    public Object eval(final String expression) {
        final Script parsed = scriptCache.get(expression);
        return parsed.run();
    }

    public void reset() {
        binding.getVariables().clear();
    }

    @Override
    public void close() {
        reset();
    }

    public void put(final String variable, final Object value) {
        binding.setVariable(variable, value);
    }

    public Object get(final String variable) {
        return getF.apply(variable);
    }

    public void remove(final String variable) {
        binding.removeVariable(variable);
    }

    public boolean contains(final String variable) {
        return binding.hasVariable(variable);
    }

}

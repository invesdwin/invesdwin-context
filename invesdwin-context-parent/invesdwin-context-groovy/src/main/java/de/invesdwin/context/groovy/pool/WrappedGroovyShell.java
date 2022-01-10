package de.invesdwin.context.groovy.pool;

import java.io.Closeable;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.context.groovy.GroovyProperties;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;

@NotThreadSafe
public class WrappedGroovyShell implements Closeable {

    private final LoadingCache<String, Script> scriptCache;

    private final GroovyShell engine;
    private final Binding binding;

    public WrappedGroovyShell() {
        final CompilerConfiguration config = new CompilerConfiguration();
        if (GroovyProperties.COMPILE_STATIC) {
            config.addCompilationCustomizers(new ASTTransformationCustomizer(CompileStatic.class));
        }
        if (GroovyProperties.TYPE_CHECKED) {
            config.addCompilationCustomizers(new ASTTransformationCustomizer(TypeChecked.class));
        }
        this.binding = new Binding(new LinkedHashMap<>());
        this.engine = new GroovyShell(binding, config);
        scriptCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .softValues()
                .<String, Script> build((key) -> engine.parse(key));
    }

    public GroovyShell getEngine() {
        return engine;
    }

    public Binding getBinding() {
        return binding;
    }

    public void eval(final String expression) {
        final Script parsed = scriptCache.get(expression);
        parsed.run();
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
        return binding.getVariable(variable);
    }

    public void remove(final String variable) {
        binding.removeVariable(variable);
    }

    public boolean contains(final String variable) {
        return binding.hasVariable(variable);
    }

}

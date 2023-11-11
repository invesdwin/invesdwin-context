package de.invesdwin.context.clojure.callback;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.clojure.ScriptTaskEngineClojure;
import de.invesdwin.context.clojure.pool.WrappedClojureEngine;
import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskParameters;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskParametersPool;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturns;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnsPool;
import de.invesdwin.util.lang.UUIDs;

@ThreadSafe
public class ClojureScriptTaskCallbackContext implements Closeable {

    private static final Map<String, ClojureScriptTaskCallbackContext> UUID_CONTEXT = new ConcurrentHashMap<>();

    private final String uuid;
    private final IScriptTaskCallback callback;

    private WrappedClojureEngine engine;

    public ClojureScriptTaskCallbackContext(final IScriptTaskCallback callback) {
        this.uuid = UUIDs.newPseudoRandomUUID();
        this.callback = callback;
        UUID_CONTEXT.put(uuid, this);
    }

    public static ClojureScriptTaskCallbackContext getContext(final String uuid) {
        return UUID_CONTEXT.get(uuid);
    }

    public void init(final ScriptTaskEngineClojure engine) {
        engine.getInputs().putString("clojureScriptTaskCallbackContextUuid", getUuid());
        engine.eval(new ClassPathResource(ClojureScriptTaskCallbackContext.class.getSimpleName() + ".clj",
                ClojureScriptTaskCallbackContext.class));
        this.engine = engine.unwrap();
    }

    public String getUuid() {
        return uuid;
    }

    public Object invoke(final String methodName, final Object... args) {
        final ObjectScriptTaskParameters parameters = ObjectScriptTaskParametersPool.INSTANCE.borrowObject();
        final ObjectScriptTaskReturns returns = ObjectScriptTaskReturnsPool.INSTANCE.borrowObject();
        try {
            parameters.setParameters(args);
            callback.invoke(methodName, parameters, returns);
            if (returns.isReturnExpression()) {
                return engine.eval((String) returns.getReturnValue());
            } else {
                return returns.getReturnValue();
            }
        } finally {
            ObjectScriptTaskReturnsPool.INSTANCE.returnObject(returns);
            ObjectScriptTaskParametersPool.INSTANCE.returnObject(parameters);
        }
    }

    @Override
    public void close() {
        UUID_CONTEXT.remove(uuid);
    }

}

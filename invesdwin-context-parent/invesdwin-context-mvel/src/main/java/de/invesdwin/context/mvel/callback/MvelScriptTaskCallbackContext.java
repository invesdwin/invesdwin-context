package de.invesdwin.context.mvel.callback;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.mvel2.MVEL;
import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskParameters;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskParametersPool;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturns;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnsPool;
import de.invesdwin.context.mvel.MvelProperties;
import de.invesdwin.context.mvel.ScriptTaskEngineMvel;
import de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.UUIDs;

@ThreadSafe
public class MvelScriptTaskCallbackContext implements Closeable {

    private static final Map<String, MvelScriptTaskCallbackContext> UUID_CONTEXT = new ConcurrentHashMap<>();

    private final String uuid;
    private final IScriptTaskCallback callback;
    private WrappedMvelScriptEngine engine;

    public MvelScriptTaskCallbackContext(final IScriptTaskCallback callback) {
        this.uuid = UUIDs.newPseudoRandomUUID();
        this.callback = callback;
        UUID_CONTEXT.put(uuid, this);
    }

    public static MvelScriptTaskCallbackContext getContext(final String uuid) {
        return UUID_CONTEXT.get(uuid);
    }

    public void init(final ScriptTaskEngineMvel engine) {
        this.engine = engine.unwrap();
        if (MvelProperties.isStrict()) {
            engine.getInputs().putString("mvelScriptTaskCallbackContextUuid", getUuid());
            //somehow type information gets lost if this is not defined directly in the given script
            //            engine.eval(MvelScriptTaskCallbackContext.class.getName() + " callback = "
            //                    + MvelScriptTaskCallbackContext.class.getName()
            //                    + ".getContext(mvelScriptTaskCallbackContextUuid);");
        } else {
            engine.getInputs().putString("mvelScriptTaskCallbackContextUuid", getUuid());
            engine.eval(new ClassPathResource(MvelScriptTaskCallbackContext.class.getSimpleName() + ".mvel",
                    MvelScriptTaskCallbackContext.class));
        }
    }

    public String getUuid() {
        return uuid;
    }

    public Object invoke(final String methodName, final List<Object> args) {
        return invoke(methodName, args.toArray(Objects.EMPTY_ARRAY));
    }

    public Object invoke(final String methodName, final Object[] args) {
        final ObjectScriptTaskParameters parameters = ObjectScriptTaskParametersPool.INSTANCE.borrowObject();
        final ObjectScriptTaskReturns returns = ObjectScriptTaskReturnsPool.INSTANCE.borrowObject();
        try {
            parameters.setParameters(args);
            callback.invoke(methodName, parameters, returns);
            if (returns.isReturnExpression()) {
                return MVEL.eval((String) returns.getReturnValue(), engine.getBinding());
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

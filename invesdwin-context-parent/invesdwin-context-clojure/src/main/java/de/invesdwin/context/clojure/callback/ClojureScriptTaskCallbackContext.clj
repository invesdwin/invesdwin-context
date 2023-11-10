<T> T callback(String methodName, Object... parameters) {
    if(!bindings.containsKey("clojureScriptTaskCallbackContext")) {
        if(bindings.containsKey("clojureScriptTaskCallbackContextUuid")) {
            bindings.put("clojureScriptTaskCallbackContext", de.invesdwin.context.clojure.callback.ClojureScriptTaskCallbackContext.getContext(clojureScriptTaskCallbackContextUuid));
        } else {
            throw new RuntimeException("IScriptTaskCallback not available");
        }
    }
    de.invesdwin.context.clojure.callback.ClojureScriptTaskCallbackContext context = (de.invesdwin.context.clojure.callback.ClojureScriptTaskCallbackContext) bindings.get("clojureScriptTaskCallbackContext");
    de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue returnValue = context.invoke(methodName, parameters);
    if(returnValue.isReturnExpression()) {
    	de.invesdwin.context.clojure.pool.WrappedClojureScriptEngine engine = de.invesdwin.context.clojure.pool.ClojureScriptEngineObjectPool.INSTANCE.borrowObject();
    	try {
        	return (T) engine.eval((String) returnValue.getReturnValue(), bindings);
        } finally {
        	de.invesdwin.context.clojure.pool.ClojureScriptEngineObjectPool.INSTANCE.returnObject(engine);
        }
    } else {
        return (T) returnValue.getReturnValue();
    }
}
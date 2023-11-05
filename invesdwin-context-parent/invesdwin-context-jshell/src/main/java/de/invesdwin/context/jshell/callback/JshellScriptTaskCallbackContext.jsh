<T> T callback(String methodName, Object... parameters) {
    if(!bindings.containsKey("jshellScriptTaskCallbackContext")) {
        if(bindings.containsKey("jshellScriptTaskCallbackContextUuid")) {
            bindings.put("jshellScriptTaskCallbackContext", de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext.getContext(jshellScriptTaskCallbackContextUuid));
        } else {
            throw new RuntimeException("IScriptTaskCallback not available");
        }
    }
    de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext context = (de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext) bindings.get("jshellScriptTaskCallbackContext");
    de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue returnValue = context.invoke(methodName, parameters);
    if(returnValue.isReturnExpression()) {
    	de.invesdwin.context.jshell.pool.WrappedJshellScriptEngine engine = de.invesdwin.context.jshell.pool.JshellScriptEngineObjectPool.INSTANCE.borrowObject();
        return (T) engine.eval((String) returnValue.getReturnValue(), bindings);
    } else {
        return (T) returnValue.getReturnValue();
    }
}
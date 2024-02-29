<T> T callback(String methodName, Object... parameters) {
    if(!binding.containsKey("jshellScriptTaskCallbackContext")) {
        if(binding.containsKey("jshellScriptTaskCallbackContextUuid")) {
            binding.put("jshellScriptTaskCallbackContext", de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext.getContext(jshellScriptTaskCallbackContextUuid));
        } else {
            throw new RuntimeException("IScriptTaskCallback not available");
        }
    }
    de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext context = (de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext) binding.get("jshellScriptTaskCallbackContext");
    de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue returnValue = context.invoke(methodName, parameters);
    if(returnValue.isReturnExpression()) {
    	de.invesdwin.context.jshell.pool.WrappedJshellScriptEngine engine = de.invesdwin.context.jshell.pool.JshellScriptEngineObjectPool.INSTANCE.borrowObject();
    	try {
        	return (T) engine.eval((String) returnValue.getReturnValue(), binding);
        } finally {
        	de.invesdwin.context.jshell.pool.JshellScriptEngineObjectPool.INSTANCE.returnObject(engine);
        }
    } else {
        return (T) returnValue.getReturnValue();
    }
}
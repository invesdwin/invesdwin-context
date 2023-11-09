fun <T> callback(methodName: String, vararg parameters: Any?): T {
    if(!bindings.containsKey("kotlinScriptTaskCallbackContext")) {
        if(bindings.containsKey("kotlinScriptTaskCallbackContextUuid")) {
            bindings.put("kotlinScriptTaskCallbackContext", de.invesdwin.context.kotlin.callback.KotlinScriptTaskCallbackContext.getContext(kotlinScriptTaskCallbackContextUuid));
        } else {
            throw Exception("IScriptTaskCallback not available");
        }
    }
    val context: de.invesdwin.context.kotlin.callback.KotlinScriptTaskCallbackContext = bindings.get("kotlinScriptTaskCallbackContext") as de.invesdwin.context.kotlin.callback.KotlinScriptTaskCallbackContext;
    val returnValue: de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue = context.invoke(methodName, *parameters);
    if(returnValue.isReturnExpression()) {
    	val engine: de.invesdwin.context.kotlin.pool.WrappedKotlinScriptEngine = de.invesdwin.context.kotlin.pool.KotlinScriptEngineObjectPool.INSTANCE.borrowObject();
    	try {
    		//can not pass bindings here, else classcastexceptions occur
        	return engine.eval(returnValue.getReturnValue() as String) as T;
        } finally {
        	de.invesdwin.context.kotlin.pool.KotlinScriptEngineObjectPool.INSTANCE.returnObject(engine);
        }
    } else {
        return returnValue.getReturnValue() as T;
    }
}
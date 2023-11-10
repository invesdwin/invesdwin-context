val cBindings: javax.script.Bindings = bindings.asInstanceOf[javax.script.Bindings]
def callback[T](methodName: String, parameters: Any*): T =
    if(!cBindings.containsKey("scalaScriptTaskCallbackContext")) {
        if(cBindings.containsKey("scalaScriptTaskCallbackContextUuid")) {
            val ctx: de.invesdwin.context.scala.callback.ScalaScriptTaskCallbackContext = de.invesdwin.context.scala.callback.ScalaScriptTaskCallbackContext.getContext(scalaScriptTaskCallbackContextUuid.asInstanceOf[String])
            cBindings.put("scalaScriptTaskCallbackContext", ctx)
        } else {
            throw new Exception("IScriptTaskCallback not available")
        }
    }
    val context: de.invesdwin.context.scala.callback.ScalaScriptTaskCallbackContext = cBindings.get("scalaScriptTaskCallbackContext").asInstanceOf[de.invesdwin.context.scala.callback.ScalaScriptTaskCallbackContext]
    val returnValue: de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue = context.invoke(methodName, parameters)
    if(returnValue.isReturnExpression()) {
    	val engine: de.invesdwin.context.scala.pool.WrappedScalaScriptEngine = de.invesdwin.context.scala.pool.ScalaScriptEngineObjectPool.INSTANCE.borrowObject()
    	try {
        	return engine.eval(returnValue.getReturnValue().asInstanceOf[String], b).asInstanceOf[T]
        } finally {
        	de.invesdwin.context.scala.pool.ScalaScriptEngineObjectPool.INSTANCE.returnObject(engine)
        }
    } else {
        return returnValue.getReturnValue().asInstanceOf[T]
    }

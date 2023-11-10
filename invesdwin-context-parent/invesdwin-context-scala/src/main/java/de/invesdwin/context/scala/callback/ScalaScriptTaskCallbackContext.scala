var scalaScriptTaskCallbackContext: de.invesdwin.context.scala.callback.ScalaScriptTaskCallbackContext = null
def callback[T](methodName: String, parameters: Any*): T = {
    if(scalaScriptTaskCallbackContext == null) {
        if(scalaScriptTaskCallbackContextUuid != null) {
            scalaScriptTaskCallbackContext = de.invesdwin.context.scala.callback.ScalaScriptTaskCallbackContext.getContext(scalaScriptTaskCallbackContextUuid.asInstanceOf[String])
        } else {
            throw new Exception("IScriptTaskCallback not available")
        }
    }
    val returnValue: de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue = scalaScriptTaskCallbackContext.invoke(methodName, parameters:_*)
    if(returnValue.isReturnExpression()) {
        val engine: de.invesdwin.context.scala.pool.WrappedScalaScriptEngine = de.invesdwin.context.scala.pool.ScalaScriptEngineObjectPool.INSTANCE.borrowObject()
        try {
            return engine.eval(returnValue.getReturnValue().asInstanceOf[String], bindings.asInstanceOf[javax.script.Bindings]).asInstanceOf[T]
        } finally {
            de.invesdwin.context.scala.pool.ScalaScriptEngineObjectPool.INSTANCE.returnObject(engine)
        }
    } else {
        return returnValue.getReturnValue().asInstanceOf[T]
    }
}

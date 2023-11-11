require 'java'
$jrubyScriptTaskCallbackContext = nil
def callback(methodName, *parameters)
    if($jrubyScriptTaskCallbackContext.nil?)
        if($bindings.containsKey("jrubyScriptTaskCallbackContextUuid"))
        	$jrubyScriptTaskCallbackContext = Java::DeInvesdwinContextJrubyCallback::JrubyScriptTaskCallbackContext.getContext($bindings.get("jrubyScriptTaskCallbackContextUuid"))
        else
			raise "IScriptTaskCallback not available"
        end
    end
    returnValue = $jrubyScriptTaskCallbackContext.invoke(methodName, parameters.to_java)
    if(returnValue.isReturnExpression())
        return eval(returnValue.getReturnValue())
    else
        return returnValue.getReturnValue()
    end
end
$trufflerubyScriptTaskCallbackContext = nil
def callback(methodName, *parameters)
    if($trufflerubyScriptTaskCallbackContext.nil?)
        if($binding.containsKey("trufflerubyScriptTaskCallbackContextUuid"))
        	Java.import 'de.invesdwin.context.ruby.truffleruby.callback.TrufflerubyScriptTaskCallbackContext'
        	$trufflerubyScriptTaskCallbackContext = TrufflerubyScriptTaskCallbackContext.getContext($binding.get("trufflerubyScriptTaskCallbackContextUuid"))
        else
			raise "IScriptTaskCallback not available"
        end
    end
    returnValue = $trufflerubyScriptTaskCallbackContext.invoke(methodName, parameters)
    if(returnValue.isReturnExpression())
        return eval(returnValue.getReturnValue())
    else
        return returnValue.getReturnValue()
    end
end
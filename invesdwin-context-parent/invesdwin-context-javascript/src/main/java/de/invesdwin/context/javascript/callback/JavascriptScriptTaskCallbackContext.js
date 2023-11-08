//https://stackoverflow.com/a/5786899
var javascriptScriptTaskCallbackContext = undefined;
function callback(methodName, ...parameters) {
    if(typeof javascriptScriptTaskCallbackContext === 'undefined') {
        if(typeof javascriptScriptTaskCallbackContextUuid !== 'undefined') {
            javascriptScriptTaskCallbackContext = Java.type('de.invesdwin.context.javascript.callback.JavascriptScriptTaskCallbackContext').getContext(javascriptScriptTaskCallbackContextUuid);
        } else {
            throw "IScriptTaskCallback not available";
        }
    }
    returnValue = javascriptScriptTaskCallbackContext.invoke(methodName, parameters);
    if(returnValue.isReturnExpression()) {
        return eval(returnValue.getReturnValue());
    } else {
        return returnValue.getReturnValue();
    }
}
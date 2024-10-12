print("putUuid")
print(putUuid)

SimpleCallbackTest = Java.type('de.invesdwin.context.javascript.tests.callback.SimpleCallbackTest')
getSecretStaticImport = SimpleCallbackTest.getSecretStatic(putUuid)
print("getSecretStaticImport")
print(getSecretStaticImport)

getSecretStaticCallback = callback("getSecretStatic", putUuid)
print("getSecretStaticCallback")
print(getSecretStaticCallback)

getSecretCallback = callback("getSecret", putUuid)
print("getSecretCallback")
print(getSecretCallback)

getSecretExpressionCallback = callback("getSecretExpression", putUuid)
print("getSecretExpressionCallback")
print(getSecretExpressionCallback)

callback("voidMethod")

callManyParams = callback("callManyParams", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParams !== 55){
	throw "callManyParams unexpected result: "+callManyParams
}
callManyParamsExpression = callback("callManyParamsExpression", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpression !== 55){
	throw "callManyParamsExpression unexpected result: "+callManyParamsExpression
}
callManyParamsExpressionMultiline = callback("callManyParamsExpressionMultiline", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpressionMultiline !== 55){
	throw "callManyParamsExpressionMultiline unexpected result: "+callManyParamsExpressionMultiline
}

getManyParamsExpression = putManyParamsExpression
print("getManyParamsExpression")
print(getManyParamsExpression)
getManyParamsExpressionMultilineWrong = putManyParamsExpressionMultilineWrong
print("getManyParamsExpressionMultilineWrong")
print(getManyParamsExpressionMultilineWrong)
getManyParamsExpressionMultiline = putManyParamsExpressionMultiline
print("getManyParamsExpressionMultiline")
print(getManyParamsExpressionMultiline)

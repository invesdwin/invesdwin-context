println("putUuid")
println(putUuid)

import de.invesdwin.context.groovy.tests.callback.SimpleCallbackTest
getSecretStaticImport = SimpleCallbackTest.getSecretStatic(putUuid)
println("getSecretStaticImport")
println(getSecretStaticImport)

getSecretStaticCallback = callback("getSecretStatic", putUuid)
println("getSecretStaticCallback")
println(getSecretStaticCallback)

getSecretCallback = callback("getSecret", putUuid)
println("getSecretCallback")
println(getSecretCallback)

getSecretExpressionCallback = callback("getSecretExpression", putUuid)
println("getSecretExpressionCallback")
println(getSecretExpressionCallback)

callback("voidMethod")

double callManyParams = callback("callManyParams", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParams != 55){
	throw new Exception("callManyParams unexpected result: "+callManyParams)
}
double callManyParamsExpression = callback("callManyParamsExpression", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpression != 55){
	throw new Exception("callManyParamsExpression unexpected result: "+callManyParamsExpression)
}
double callManyParamsExpressionMultiline = callback("callManyParamsExpressionMultiline", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpressionMultiline != 55){
	throw new Exception("callManyParams unexpected result: "+callManyParamsExpressionMultiline)
}

getManyParamsExpression = putManyParamsExpression
println("getManyParamsExpression")
println(getManyParamsExpression)
getManyParamsExpressionMultilineWrong = putManyParamsExpressionMultilineWrong
println("getManyParamsExpressionMultilineWrong")
println(getManyParamsExpressionMultilineWrong)
getManyParamsExpressionMultiline = putManyParamsExpressionMultiline
println("getManyParamsExpressionMultiline")
println(getManyParamsExpressionMultiline)
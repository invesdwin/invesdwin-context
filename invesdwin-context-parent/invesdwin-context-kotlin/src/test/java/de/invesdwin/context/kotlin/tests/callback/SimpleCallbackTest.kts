import de.invesdwin.context.kotlin.tests.callback.SimpleCallbackTest

println("putUuid")
println(putUuid)

val getSecretStaticImport = SimpleCallbackTest.getSecretStatic(putUuid)
println("getSecretStaticImport")
println(getSecretStaticImport)

val getSecretStaticCallback: String = callback("getSecretStatic", putUuid)
println("getSecretStaticCallback")
println(getSecretStaticCallback)

val getSecretCallback: String = callback("getSecret", putUuid)
println("getSecretCallback")
println(getSecretCallback)

val getSecretExpressionCallback: String = callback("getSecretExpression", putUuid)
println("getSecretExpressionCallback")
println(getSecretExpressionCallback)

callback<Unit>("voidMethod")

val callManyParams: Double = callback("callManyParams", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParams != 55.0){
	throw Exception("callManyParams unexpected result: "+callManyParams)
}
val callManyParamsExpression: Double = callback("callManyParamsExpression", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpression != 55.0){
	throw Exception("callManyParamsExpression unexpected result: "+callManyParamsExpression)
}
val callManyParamsExpressionMultiline: Double = callback("callManyParamsExpressionMultiline", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpressionMultiline != 55.0){
	throw Exception("callManyParams unexpected result: "+callManyParamsExpressionMultiline)
}

val getManyParamsExpression: Double = putManyParamsExpression
println("getManyParamsExpression")
println(getManyParamsExpression)
val getManyParamsExpressionMultilineWrong: Double = putManyParamsExpressionMultilineWrong
println("getManyParamsExpressionMultilineWrong")
println(getManyParamsExpressionMultilineWrong)
val getManyParamsExpressionMultiline: Double = putManyParamsExpressionMultiline
println("getManyParamsExpressionMultiline")
println(getManyParamsExpressionMultiline)

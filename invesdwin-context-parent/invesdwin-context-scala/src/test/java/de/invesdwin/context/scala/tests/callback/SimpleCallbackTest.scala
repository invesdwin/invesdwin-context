import de.invesdwin.context.scala.tests.callback.SimpleCallbackTest

System.out.println("putUuid")
System.out.println(putUuid)

val getSecretStaticImport = SimpleCallbackTest.getSecretStatic(String.valueOf(putUuid))
System.out.println("getSecretStaticImport")
System.out.println(getSecretStaticImport)

val getSecretStaticCallback: String = callback("getSecretStatic", putUuid)
System.out.println("getSecretStaticCallback")
System.out.println(getSecretStaticCallback)

val getSecretCallback: String = callback("getSecret", putUuid)
System.out.println("getSecretCallback")
System.out.println(getSecretCallback)

val getSecretExpressionCallback: String = callback("getSecretExpression", putUuid)
System.out.println("getSecretExpressionCallback")
System.out.println(getSecretExpressionCallback)

callback("voidMethod")

val callManyParams: Double = callback("callManyParams", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParams != 55){
	throw new Exception("callManyParams unexpected result: "+callManyParams)
}
val callManyParamsExpression: Double = callback("callManyParamsExpression", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpression != 55){
	throw new Exception("callManyParamsExpression unexpected result: "+callManyParamsExpression)
}
val callManyParamsExpressionMultiline: Double = callback("callManyParamsExpressionMultiline", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if(callManyParamsExpressionMultiline != 55){
	throw new Exception("callManyParamsExpressionMultiline unexpected result: "+callManyParamsExpressionMultiline)
}

val getManyParamsExpression: Double = putManyParamsExpression
System.out.println("getManyParamsExpression")
System.out.println(getManyParamsExpression)
val getManyParamsExpressionMultilineWrong: Double = putManyParamsExpressionMultilineWrong
System.out.println("getManyParamsExpressionMultilineWrong")
System.out.println(getManyParamsExpressionMultilineWrong)
val getManyParamsExpressionMultiline: Double = putManyParamsExpressionMultiline
System.out.println("getManyParamsExpressionMultiline")
System.out.println(getManyParamsExpressionMultiline)

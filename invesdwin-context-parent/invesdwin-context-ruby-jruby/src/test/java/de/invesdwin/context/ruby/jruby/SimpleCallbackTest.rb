puts("putUuid")
puts(putUuid)

java_import Java::DeInvesdwinContextRubyJruby::SimpleCallbackTest
getSecretStaticImport = SimpleCallbackTest.getSecretStatic(putUuid)
puts("getSecretStaticImport")
puts(getSecretStaticImport)

getSecretStaticCallback = callback("getSecretStatic", putUuid)
puts("getSecretStaticCallback")
puts(getSecretStaticCallback)

getSecretCallback = callback("getSecret", putUuid)
puts("getSecretCallback")
puts(getSecretCallback)

getSecretExpressionCallback = callback("getSecretExpression", putUuid)
puts("getSecretExpressionCallback")
puts(getSecretExpressionCallback)

callback("voidMethod")

callManyParams = callback("callManyParams", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if callManyParams != 55
	raise "callManyParams unexpected result: "+callManyParams
end
callManyParamsExpression = callback("callManyParamsExpression", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if callManyParamsExpression != 55
	raise "callManyParamsExpression unexpected result: "+callManyParamsExpression
end
callManyParamsExpressionMultiline = callback("callManyParamsExpressionMultiline", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0)
if callManyParamsExpressionMultiline != 55
	raise "callManyParamsExpressionMultiline unexpected result: "+callManyParamsExpressionMultiline
end

getManyParamsExpression = putManyParamsExpression
puts("getManyParamsExpression")
puts(getManyParamsExpression)
getManyParamsExpressionMultilineWrong = putManyParamsExpressionMultilineWrong
puts("getManyParamsExpressionMultilineWrong")
puts(getManyParamsExpressionMultilineWrong)
getManyParamsExpressionMultiline = putManyParamsExpressionMultiline
puts("getManyParamsExpressionMultiline")
puts(getManyParamsExpressionMultiline)

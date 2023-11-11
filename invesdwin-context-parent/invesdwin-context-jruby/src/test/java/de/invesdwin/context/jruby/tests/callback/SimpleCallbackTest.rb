puts("putUuid")
puts(putUuid)

java_import Java::DeInvesdwinContextJrubyTestsCallback::SimpleCallbackTest
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
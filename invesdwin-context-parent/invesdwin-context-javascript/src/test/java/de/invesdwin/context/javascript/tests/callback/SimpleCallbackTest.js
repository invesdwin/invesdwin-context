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
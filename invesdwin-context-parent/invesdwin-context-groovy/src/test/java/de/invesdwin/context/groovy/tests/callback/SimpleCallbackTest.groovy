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
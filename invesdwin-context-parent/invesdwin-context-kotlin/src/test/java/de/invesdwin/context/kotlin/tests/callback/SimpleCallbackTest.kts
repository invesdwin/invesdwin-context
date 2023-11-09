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
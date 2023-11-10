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

callback[Unit]("voidMethod")
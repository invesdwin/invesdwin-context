System.out.println("putUuid");
System.out.println(putUuid);

import de.invesdwin.context.beanshell.tests.callback.SimpleCallbackTest;
var getSecretStaticImport = SimpleCallbackTest.getSecretStatic(putUuid);
System.out.println("getSecretStaticImport");
System.out.println(getSecretStaticImport);

var getSecretStaticCallback = callback("getSecretStatic", putUuid);
System.out.println("getSecretStaticCallback");
System.out.println(getSecretStaticCallback);

var getSecretCallback = callback("getSecret", putUuid);
System.out.println("getSecretCallback");
System.out.println(getSecretCallback);

var getSecretExpressionCallback = callback("getSecretExpression", putUuid);
System.out.println("getSecretExpressionCallback");
System.out.println(getSecretExpressionCallback);

callback("voidMethod");
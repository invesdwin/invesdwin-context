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

var callManyParams = callback("callManyParams", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0);
if(callManyParams != 55){
	throw new Exception("callManyParams unexpected result: "+callManyParams);
}
var callManyParamsExpression = callback("callManyParamsExpression", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0);
if(callManyParamsExpression != 55){
	throw new Exception("callManyParamsExpression unexpected result: "+callManyParamsExpression);
}
var callManyParamsExpressionMultiline = callback("callManyParamsExpressionMultiline", true, 2, 3, '4', 5, 6, 7.0, 8.0, "123456789", 10.0);
if(callManyParamsExpressionMultiline != 55){
	throw new Exception("callManyParams unexpected result: "+callManyParamsExpressionMultiline);
}

var getManyParamsExpression = putManyParamsExpression;
System.out.println("getManyParamsExpression");
System.out.println(getManyParamsExpression);
var getManyParamsExpressionMultilineWrong = putManyParamsExpressionMultilineWrong;
System.out.println("getManyParamsExpressionMultilineWrong");
System.out.println(getManyParamsExpressionMultilineWrong);
var getManyParamsExpressionMultiline = putManyParamsExpressionMultiline;
System.out.println("getManyParamsExpressionMultiline");
System.out.println(getManyParamsExpressionMultiline);

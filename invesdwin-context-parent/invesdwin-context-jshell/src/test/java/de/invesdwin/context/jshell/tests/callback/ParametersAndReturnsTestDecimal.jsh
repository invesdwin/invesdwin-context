System.out.println("getDecimal");
if(bindings.containsKey("getDecimal"))
	throw new Exception("getDecimal already defined!");
var getDecimal = callback("getDecimal");
var getDecimalType = getDecimal.getClass();
System.out.println(getDecimalType);
System.out.println(getDecimal);
if(getDecimalType != Double.class)
	throw new Exception("getDecimal not Double!");
callback("setDecimal",getDecimal);

System.out.println("getDecimalVector");
if(bindings.containsKey("getDecimalVector"))
	throw new Exception("getDecimalVector already defined!");
var getDecimalVector = callback("getDecimalVector");
var getDecimalVectorType = getDecimalVector.getClass().getComponentType();
System.out.println(getDecimalVectorType);
System.out.println(getDecimalVector);
if(getDecimalVectorType != double.class)
	throw new Exception("getDecimalVector not Double!");
callback("setDecimalVector",getDecimalVector);

System.out.println("getDecimalVectorAsList");
if(bindings.containsKey("getDecimalVectorAsList"))
	throw new Exception("getDecimalVectorAsList already defined!");
var getDecimalVectorAsList = callback("getDecimalVectorAsList");
var getDecimalVectorAsListType = getDecimalVectorAsList.getClass().getComponentType();
System.out.println(getDecimalVectorAsListType);
System.out.println(getDecimalVectorAsList);
if(getDecimalVectorAsListType != double.class)
	throw new Exception("getDecimalVectorAsList not Double!");
callback("setDecimalVectorAsList",getDecimalVectorAsList);

System.out.println("getDecimalMatrix");
if(bindings.containsKey("getDecimalMatrix"))
	throw new Exception("getDecimalMatrix already defined!");
var getDecimalMatrix = callback("getDecimalMatrix");
var getDecimalMatrixType = getDecimalMatrix[0].getClass().getComponentType();
System.out.println(getDecimalMatrixType);
System.out.println(getDecimalMatrix);
if(getDecimalMatrixType != double.class)
	throw new Exception("getDecimalMatrix not Double!");
callback("setDecimalMatrix",getDecimalMatrix);

System.out.println("getDecimalMatrixAsList");
if(bindings.containsKey("getDecimalMatrixAsList"))
	throw new Exception("getDecimalMatrixAsList already defined!");
var getDecimalMatrixAsList = callback("getDecimalMatrixAsList");
var getDecimalMatrixAsListType = getDecimalMatrixAsList[0].getClass().getComponentType();
System.out.println(getDecimalMatrixAsListType);
System.out.println(getDecimalMatrixAsList);
if(getDecimalMatrixAsListType != double.class)
	throw new Exception("getDecimalMatrixAsList not Double!");
callback("setDecimalMatrixAsList",getDecimalMatrixAsList);

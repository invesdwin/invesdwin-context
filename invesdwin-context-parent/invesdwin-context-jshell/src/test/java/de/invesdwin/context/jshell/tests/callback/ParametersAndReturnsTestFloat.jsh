System.out.println("getFloat");
if(bindings.containsKey("getFloat"))
	throw new Exception("getFloat already defined!");
var getFloat = callback("getFloat");
var getFloatType = getFloat.getClass();
System.out.println(getFloatType);
System.out.println(getFloat);
if(getFloatType != Float.class)
	throw new Exception("getFloat not Float!");
callback("setFloat",getFloat);

System.out.println("getFloatVector");
if(bindings.containsKey("getFloatVector"))
	throw new Exception("getFloatVector already defined!");
var getFloatVector = callback("getFloatVector");
var getFloatVectorType = getFloatVector.getClass().getComponentType();
System.out.println(getFloatVectorType);
System.out.println(getFloatVector);
if(getFloatVectorType != float.class)
	throw new Exception("getFloatVector not Float!");
callback("setFloatVector",getFloatVector);

System.out.println("getFloatVectorAsList");
if(bindings.containsKey("getFloatVectorAsList"))
	throw new Exception("getFloatVectorAsList already defined!");
var getFloatVectorAsList = callback("getFloatVectorAsList");
var getFloatVectorAsListType = getFloatVectorAsList.getClass().getComponentType();
System.out.println(getFloatVectorAsListType);
System.out.println(getFloatVectorAsList);
if(getFloatVectorAsListType != float.class)
	throw new Exception("getFloatVectorAsList not Float!");
callback("setFloatVectorAsList",getFloatVectorAsList);

System.out.println("getFloatMatrix");
if(bindings.containsKey("getFloatMatrix"))
	throw new Exception("getFloatMatrix already defined!");
var getFloatMatrix = callback("getFloatMatrix");
var getFloatMatrixType = getFloatMatrix[0].getClass().getComponentType();
System.out.println(getFloatMatrixType);
System.out.println(getFloatMatrix);
if(getFloatMatrixType != float.class)
	throw new Exception("getFloatMatrix not Float!");
callback("setFloatMatrix",getFloatMatrix);

System.out.println("getFloatMatrixAsList");
if(bindings.containsKey("getFloatMatrixAsList"))
	throw new Exception("getFloatMatrixAsList already defined!");
var getFloatMatrixAsList = callback("getFloatMatrixAsList");
var getFloatMatrixAsListType = getFloatMatrixAsList[0].getClass().getComponentType();
System.out.println(getFloatMatrixAsListType);
System.out.println(getFloatMatrixAsList);
if(getFloatMatrixAsListType != float.class)
	throw new Exception("getFloatMatrixAsList not Float!");
callback("setFloatMatrixAsList",getFloatMatrixAsList);

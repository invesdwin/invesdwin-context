System.out.println("getFloat");
if(getFloat != void)
	throw new Exception("getFloat already defined!");
getFloat = callback("getFloat");
getFloatType = getFloat.getClass();
System.out.println(getFloatType);
System.out.println(getFloat);
if(getFloatType != Float.class)
	throw new Exception("getFloat not Float!");
callback("setFloat",getFloat);

System.out.println("getFloatVector");
if(getFloatVector != void)
	throw new Exception("getFloatVector already defined!");
getFloatVector = callback("getFloatVector");
getFloatVectorType = getFloatVector.getClass().getComponentType();
System.out.println(getFloatVectorType);
System.out.println(getFloatVector);
if(getFloatVectorType != float.class)
	throw new Exception("getFloatVector not Float!");
callback("setFloatVector",getFloatVector);

System.out.println("getFloatVectorAsList");
if(getFloatVectorAsList != void)
	throw new Exception("getFloatVectorAsList already defined!");
getFloatVectorAsList = callback("getFloatVectorAsList");
getFloatVectorAsListType = getFloatVectorAsList.getClass().getComponentType();
System.out.println(getFloatVectorAsListType);
System.out.println(getFloatVectorAsList);
if(getFloatVectorAsListType != float.class)
	throw new Exception("getFloatVectorAsList not Float!");
callback("setFloatVectorAsList",getFloatVectorAsList);

System.out.println("getFloatMatrix");
if(getFloatMatrix != void)
	throw new Exception("getFloatMatrix already defined!");
getFloatMatrix = callback("getFloatMatrix");
getFloatMatrixType = getFloatMatrix[0].getClass().getComponentType();
System.out.println(getFloatMatrixType);
System.out.println(getFloatMatrix);
if(getFloatMatrixType != float.class)
	throw new Exception("getFloatMatrix not Float!");
callback("setFloatMatrix",new Object[]{getFloatMatrix});

System.out.println("getFloatMatrixAsList");
if(getFloatMatrixAsList != void)
	throw new Exception("getFloatMatrixAsList already defined!");
getFloatMatrixAsList = callback("getFloatMatrixAsList");
getFloatMatrixAsListType = getFloatMatrixAsList[0].getClass().getComponentType();
System.out.println(getFloatMatrixAsListType);
System.out.println(getFloatMatrixAsList);
if(getFloatMatrixAsListType != float.class)
	throw new Exception("getFloatMatrixAsList not Float!");
callback("setFloatMatrixAsList",new Object[]{getFloatMatrixAsList});

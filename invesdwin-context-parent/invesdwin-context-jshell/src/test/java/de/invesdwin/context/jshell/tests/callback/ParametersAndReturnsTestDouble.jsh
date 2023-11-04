System.out.println("getDouble");
if(getDouble != void)
	throw new Exception("getDouble already defined!");
getDouble = callback("getDouble");
getDoubleType = getDouble.getClass();
System.out.println(getDoubleType);
System.out.println(getDouble);
if(getDoubleType != Double.class)
	throw new Exception("getDouble not Double!");
callback("setDouble",getDouble);

System.out.println("getDoubleVector");
if(getDoubleVector != void)
	throw new Exception("getDoubleVector already defined!");
getDoubleVector = callback("getDoubleVector");
getDoubleVectorType = getDoubleVector.getClass().getComponentType();
System.out.println(getDoubleVectorType);
System.out.println(getDoubleVector);
if(getDoubleVectorType != double.class)
	throw new Exception("getDoubleVector not Double!");
callback("setDoubleVector",getDoubleVector);

System.out.println("getDoubleVectorAsList");
if(getDoubleVectorAsList != void)
	throw new Exception("getDoubleVectorAsList already defined!");
getDoubleVectorAsList = callback("getDoubleVectorAsList");
getDoubleVectorAsListType = getDoubleVectorAsList.getClass().getComponentType();
System.out.println(getDoubleVectorAsListType);
System.out.println(getDoubleVectorAsList);
if(getDoubleVectorAsListType != double.class)
	throw new Exception("getDoubleVectorAsList not Double!");
callback("setDoubleVectorAsList",getDoubleVectorAsList);

System.out.println("getDoubleMatrix");
if(getDoubleMatrix != void)
	throw new Exception("getDoubleMatrix already defined!");
getDoubleMatrix = callback("getDoubleMatrix");
getDoubleMatrixType = getDoubleMatrix[0].getClass().getComponentType();
System.out.println(getDoubleMatrixType);
System.out.println(getDoubleMatrix);
if(getDoubleMatrixType != double.class)
	throw new Exception("getDoubleMatrix not Double!");
callback("setDoubleMatrix",new Object[]{getDoubleMatrix});

System.out.println("getDoubleMatrixAsList");
if(getDoubleMatrixAsList != void)
	throw new Exception("getDoubleMatrixAsList already defined!");
getDoubleMatrixAsList = callback("getDoubleMatrixAsList");
getDoubleMatrixAsListType = getDoubleMatrixAsList[0].getClass().getComponentType();
System.out.println(getDoubleMatrixAsListType);
System.out.println(getDoubleMatrixAsList);
if(getDoubleMatrixAsListType != double.class)
	throw new Exception("getDoubleMatrixAsList not Double!");
callback("setDoubleMatrixAsList",new Object[]{getDoubleMatrixAsList});

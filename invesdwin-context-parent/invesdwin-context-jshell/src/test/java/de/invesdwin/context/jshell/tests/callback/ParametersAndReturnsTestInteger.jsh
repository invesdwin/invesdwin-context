System.out.println("getInteger");
if(getInteger != void)
	throw new Exception("getInteger already defined!");
getInteger = callback("getInteger");
getIntegerType = getInteger.getClass();
System.out.println(getIntegerType);
System.out.println(getInteger);
if(getIntegerType != Integer.class)
	throw new Exception("getInteger not Integer!");
callback("setInteger",getInteger);

System.out.println("getIntegerVector");
if(getIntegerVector != void)
	throw new Exception("getIntegerVector already defined!");
getIntegerVector = callback("getIntegerVector");
getIntegerVectorType = getIntegerVector.getClass().getComponentType();
System.out.println(getIntegerVectorType);
System.out.println(getIntegerVector);
if(getIntegerVectorType != int.class)
	throw new Exception("getIntegerVector not Integer!");
callback("setIntegerVector",getIntegerVector);

System.out.println("getIntegerVectorAsList");
if(getIntegerVectorAsList != void)
	throw new Exception("getIntegerVectorAsList already defined!");
getIntegerVectorAsList = callback("getIntegerVectorAsList");
getIntegerVectorAsListType = getIntegerVectorAsList.getClass().getComponentType();
System.out.println(getIntegerVectorAsListType);
System.out.println(getIntegerVectorAsList);
if(getIntegerVectorAsListType != int.class)
	throw new Exception("getIntegerVectorAsList not Integer!");
callback("setIntegerVectorAsList",getIntegerVectorAsList);

System.out.println("getIntegerMatrix");
if(getIntegerMatrix != void)
	throw new Exception("getIntegerMatrix already defined!");
getIntegerMatrix = callback("getIntegerMatrix");
getIntegerMatrixType = getIntegerMatrix[0].getClass().getComponentType();
System.out.println(getIntegerMatrixType);
System.out.println(getIntegerMatrix);
if(getIntegerMatrixType != int.class)
	throw new Exception("getIntegerMatrix not Integer!");
callback("setIntegerMatrix",new Object[]{getIntegerMatrix});

System.out.println("getIntegerMatrixAsList");
if(getIntegerMatrixAsList != void)
	throw new Exception("getIntegerMatrixAsList already defined!");
getIntegerMatrixAsList = callback("getIntegerMatrixAsList");
getIntegerMatrixAsListType = getIntegerMatrixAsList[0].getClass().getComponentType();
System.out.println(getIntegerMatrixAsListType);
System.out.println(getIntegerMatrixAsList);
if(getIntegerMatrixAsListType != int.class)
	throw new Exception("getIntegerMatrixAsList not Integer!");
callback("setIntegerMatrixAsList",new Object[]{getIntegerMatrixAsList});

System.out.println("getShort");
if(getShort != void)
	throw new Exception("getShort already defined!");
getShort = callback("getShort");
getShortType = getShort.getClass();
System.out.println(getShortType);
System.out.println(getShort);
if(getShortType != Short.class)
	throw new Exception("getShort not Short!");
callback("setShort",getShort);

System.out.println("getShortVector");
if(getShortVector != void)
	throw new Exception("getShortVector already defined!");
getShortVector = callback("getShortVector");
getShortVectorType = getShortVector.getClass().getComponentType();
System.out.println(getShortVectorType);
System.out.println(getShortVector);
if(getShortVectorType != short.class)
	throw new Exception("getShortVector not Short!");
callback("setShortVector",getShortVector);

System.out.println("getShortVectorAsList");
if(getShortVectorAsList != void)
	throw new Exception("getShortVectorAsList already defined!");
getShortVectorAsList = callback("getShortVectorAsList");
getShortVectorAsListType = getShortVectorAsList.getClass().getComponentType();
System.out.println(getShortVectorAsListType);
System.out.println(getShortVectorAsList);
if(getShortVectorAsListType != short.class)
	throw new Exception("getShortVectorAsList not Short!");
callback("setShortVectorAsList",getShortVectorAsList);

System.out.println("getShortMatrix");
if(getShortMatrix != void)
	throw new Exception("getShortMatrix already defined!");
getShortMatrix = callback("getShortMatrix");
getShortMatrixType = getShortMatrix[0].getClass().getComponentType();
System.out.println(getShortMatrixType);
System.out.println(getShortMatrix);
if(getShortMatrixType != short.class)
	throw new Exception("getShortMatrix not Short!");
callback("setShortMatrix",new Object[]{getShortMatrix});

System.out.println("getShortMatrixAsList");
if(getShortMatrixAsList != void)
	throw new Exception("getShortMatrixAsList already defined!");
getShortMatrixAsList = callback("getShortMatrixAsList");
getShortMatrixAsListType = getShortMatrixAsList[0].getClass().getComponentType();
System.out.println(getShortMatrixAsListType);
System.out.println(getShortMatrixAsList);
if(getShortMatrixAsListType != short.class)
	throw new Exception("getShortMatrixAsList not Short!");
callback("setShortMatrixAsList",new Object[]{getShortMatrixAsList});

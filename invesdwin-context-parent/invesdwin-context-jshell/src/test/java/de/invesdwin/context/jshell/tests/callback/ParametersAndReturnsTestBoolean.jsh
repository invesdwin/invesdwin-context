System.out.println("getBoolean");
if(getBoolean != void)
	throw new Exception("getBoolean already defined!");
getBoolean = callback("getBoolean");
getBooleanType = getBoolean.getClass();
System.out.println(getBooleanType);
System.out.println(getBoolean);
if(getBooleanType != Boolean.class)
	throw new Exception("getBoolean not Boolean!");
callback("setBoolean",getBoolean);

System.out.println("getBooleanVector");
if(getBooleanVector != void)
	throw new Exception("getBooleanVector already defined!");
getBooleanVector = callback("getBooleanVector");
getBooleanVectorType = getBooleanVector.getClass().getComponentType();
System.out.println(getBooleanVectorType);
System.out.println(getBooleanVector);
if(getBooleanVectorType != boolean.class)
	throw new Exception("getBooleanVector not Boolean!");
callback("setBooleanVector",getBooleanVector);

System.out.println("getBooleanVectorAsList");
if(getBooleanVectorAsList != void)
	throw new Exception("getBooleanVectorAsList already defined!");
getBooleanVectorAsList = callback("getBooleanVectorAsList");
getBooleanVectorAsListType = getBooleanVectorAsList.getClass().getComponentType();
System.out.println(getBooleanVectorAsListType);
System.out.println(getBooleanVectorAsList);
if(getBooleanVectorAsListType != boolean.class)
	throw new Exception("getBooleanVectorAsList not Boolean!");
callback("setBooleanVectorAsList",getBooleanVectorAsList);

System.out.println("getBooleanMatrix");
if(getBooleanMatrix != void)
	throw new Exception("getBooleanMatrix already defined!");
getBooleanMatrix = callback("getBooleanMatrix");
getBooleanMatrixType = getBooleanMatrix[0].getClass().getComponentType();
System.out.println(getBooleanMatrixType);
System.out.println(getBooleanMatrix);
if(getBooleanMatrixType != boolean.class)
	throw new Exception("getBooleanMatrix not Boolean!");
callback("setBooleanMatrix",new Object[]{getBooleanMatrix});

System.out.println("getBooleanMatrixAsList");
if(getBooleanMatrixAsList != void)
	throw new Exception("getBooleanMatrixAsList already defined!");
getBooleanMatrixAsList = callback("getBooleanMatrixAsList");
getBooleanMatrixAsListType = getBooleanMatrixAsList[0].getClass().getComponentType();
System.out.println(getBooleanMatrixAsListType);
System.out.println(getBooleanMatrixAsList);
if(getBooleanMatrixAsListType != boolean.class)
	throw new Exception("getBooleanMatrixAsList not Boolean!");
callback("setBooleanMatrixAsList",new Object[]{getBooleanMatrixAsList});

System.out.println("getByte");
if(getByte != void)
	throw new Exception("getByte already defined!");
getByte = callback("getByte");
getByteType = getByte.getClass();
System.out.println(getByteType);
System.out.println(getByte);
if(getByteType != Byte.class)
	throw new Exception("getByte not Byte!");
callback("setByte",getByte);

System.out.println("getByteVector");
if(getByteVector != void)
	throw new Exception("getByteVector already defined!");
getByteVector = callback("getByteVector");
getByteVectorType = getByteVector.getClass().getComponentType();
System.out.println(getByteVectorType);
System.out.println(getByteVector);
if(getByteVectorType != byte.class)
	throw new Exception("getByteVector not Byte!");
callback("setByteVector",getByteVector);

System.out.println("getByteVectorAsList");
if(getByteVectorAsList != void)
	throw new Exception("getByteVectorAsList already defined!");
getByteVectorAsList = callback("getByteVectorAsList");
getByteVectorAsListType = getByteVectorAsList.getClass().getComponentType();
System.out.println(getByteVectorAsListType);
System.out.println(getByteVectorAsList);
if(getByteVectorAsListType != byte.class)
	throw new Exception("getByteVectorAsList not Byte!");
callback("setByteVectorAsList",getByteVectorAsList);

System.out.println("getByteMatrix");
if(getByteMatrix != void)
	throw new Exception("getByteMatrix already defined!");
getByteMatrix = callback("getByteMatrix");
getByteMatrixType = getByteMatrix[0].getClass().getComponentType();
System.out.println(getByteMatrixType);
System.out.println(getByteMatrix);
if(getByteMatrixType != byte.class)
	throw new Exception("getByteMatrix not Byte!");
callback("setByteMatrix",new Object[]{getByteMatrix});

System.out.println("getByteMatrixAsList");
if(getByteMatrixAsList != void)
	throw new Exception("getByteMatrixAsList already defined!");
getByteMatrixAsList = callback("getByteMatrixAsList");
getByteMatrixAsListType = getByteMatrixAsList[0].getClass().getComponentType();
System.out.println(getByteMatrixAsListType);
System.out.println(getByteMatrixAsList);
if(getByteMatrixAsListType != byte.class)
	throw new Exception("getByteMatrixAsList not Byte!");
callback("setByteMatrixAsList",new Object[]{getByteMatrixAsList});

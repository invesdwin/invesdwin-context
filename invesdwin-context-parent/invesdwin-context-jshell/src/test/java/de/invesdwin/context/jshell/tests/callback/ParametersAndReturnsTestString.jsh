System.out.println("getString");
if(getString != void)
	throw new Exception("getString already defined!");
getString = callback("getString");
getStringType = getString.getClass();
System.out.println(getStringType);
System.out.println(getString);
if(getStringType != String.class)
	throw new Exception("getString not String!");
callback("setString",getString);

System.out.println("getStringWithNull");
if(getStringWithNull != void)
	throw new Exception("getStringWithNull already defined!");
getStringWithNull = callback("getStringWithNull");
System.out.println(getStringWithNull);
if(getStringWithNull != null)
	throw new Exception("getStringWithNull not null!");
callback("setStringWithNull",getStringWithNull);

System.out.println("getStringVector");
if(getStringVector != void)
	throw new Exception("getStringVector already defined!");
getStringVector = callback("getStringVector");
getStringVectorType = getStringVector.getClass().getComponentType();
System.out.println(getStringVectorType);
System.out.println(getStringVector);
if(getStringVectorType != String.class)
	throw new Exception("getStringVector not String!");
callback("setStringVector",new Object[]{getStringVector});

System.out.println("getStringVectorWithNull");
if(getStringVectorWithNull != void)
	throw new Exception("getStringVectorWithNull already defined!");
getStringVectorWithNull = callback("getStringVectorWithNull");
getStringVectorWithNullType = getStringVectorWithNull.getClass().getComponentType();
System.out.println(getStringVectorWithNullType);
System.out.println(getStringVectorWithNull);
if(getStringVectorWithNullType != String.class)
	throw new Exception("getStringVectorWithNull not String!");
if(getStringVectorWithNull[1] != null)
	throw new Exception("getStringVectorWithNull[1] not null!");
callback("setStringVectorWithNull",new Object[]{getStringVectorWithNull});

System.out.println("getStringVectorAsList");
if(getStringVectorAsList != void)
	throw new Exception("getStringVectorAsList already defined!");
getStringVectorAsList = callback("getStringVectorAsList");
getStringVectorAsListType = getStringVectorAsList.getClass().getComponentType();
System.out.println(getStringVectorAsListType);
System.out.println(getStringVectorAsList);
if(getStringVectorAsListType != String.class)
	throw new Exception("getStringVectorAsList not String!");
callback("setStringVectorAsList",new Object[]{getStringVectorAsList});

System.out.println("getStringVectorAsListWithNull");
if(getStringVectorAsListWithNull != void)
	throw new Exception("getStringVectorAsListWithNull already defined!");
getStringVectorAsListWithNull = callback("getStringVectorAsListWithNull");
getStringVectorAsListWithNullType = getStringVectorAsListWithNull.getClass().getComponentType();
System.out.println(getStringVectorAsListWithNullType);
System.out.println(getStringVectorAsListWithNull);
if(getStringVectorAsListWithNullType != String.class)
	throw new Exception("getStringVectorAsListWithNull not String!");
if(getStringVectorAsListWithNull[1] != null)
	throw new Exception("getStringVectorAsListWithNull[1] not null!");
callback("setStringVectorAsListWithNull",new Object[]{getStringVectorAsListWithNull});

System.out.println("getStringMatrix");
if(getStringMatrix != void)
	throw new Exception("getStringMatrix already defined!");
getStringMatrix = callback("getStringMatrix");
getStringMatrixType = getStringMatrix[0].getClass().getComponentType();
System.out.println(getStringMatrixType);
System.out.println(getStringMatrix);
if(getStringMatrixType != String.class)
	throw new Exception("getStringMatrix not String!");
callback("setStringMatrix",new Object[]{getStringMatrix});

System.out.println("getStringMatrixWithNull");
if(getStringMatrixWithNull != void)
	throw new Exception("getStringMatrixWithNull already defined!");
getStringMatrixWithNull = callback("getStringMatrixWithNull");
getStringMatrixWithNullType = getStringMatrixWithNull[0][1].getClass();
System.out.println(getStringMatrixWithNullType);
System.out.println(getStringMatrixWithNull);
if(getStringMatrixWithNullType != String.class)
	throw new Exception("getStringMatrixWithNull not String!");
if(getStringMatrixWithNull[0][0] != null)
	throw new Exception("getStringMatrixWithNull[0][0] not null!");
if(getStringMatrixWithNull[1][1] != null)
	throw new Exception("getStringMatrixWithNull[1][1] not null!");
if(getStringMatrixWithNull[2][2] != null)
	throw new Exception("getStringMatrixWithNull[2][2] not null!");
callback("setStringMatrixWithNull",new Object[]{getStringMatrixWithNull});

System.out.println("getStringMatrixAsList");
if(getStringMatrixAsList != void)
	throw new Exception("getStringMatrixAsList already defined!");
getStringMatrixAsList = callback("getStringMatrixAsList");
getStringMatrixAsListType = getStringMatrixAsList[0].getClass().getComponentType();
System.out.println(getStringMatrixAsListType);
System.out.println(getStringMatrixAsList);
if(getStringMatrixAsListType != String.class)
	throw new Exception("getStringMatrixAsList not String!");
callback("setStringMatrixAsList",new Object[]{getStringMatrixAsList});

System.out.println("getStringMatrixAsListWithNull");
if(getStringMatrixAsListWithNull != void)
	throw new Exception("getStringMatrixAsListWithNull already defined!");
getStringMatrixAsListWithNull = callback("getStringMatrixAsListWithNull");
getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1].getClass();
System.out.println(getStringMatrixAsListWithNullType);
System.out.println(getStringMatrixAsListWithNull);
if(getStringMatrixAsListWithNullType != String.class)
	throw new Exception("getStringMatrixAsListWithNull not String!");
if(getStringMatrixAsListWithNull[0][0] != null)
	throw new Exception("getStringMatrixAsListWithNull[0][0] not null!");
if(getStringMatrixAsListWithNull[1][1] != null)
	throw new Exception("getStringMatrixAsListWithNull[1][1] not null!");
if(getStringMatrixAsListWithNull[2][2] != null)
	throw new Exception("getStringMatrixAsListWithNull[2][2] not null!");
callback("setStringMatrixAsListWithNull",new Object[]{getStringMatrixAsListWithNull});

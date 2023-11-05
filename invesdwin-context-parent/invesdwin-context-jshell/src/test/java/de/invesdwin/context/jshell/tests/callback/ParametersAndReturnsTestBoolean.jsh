System.out.println("getBoolean");
if(bindings.containsKey("getBoolean"))
	throw new Exception("getBoolean already defined!");
var getBoolean = callback("getBoolean");
var getBooleanType = getBoolean.getClass();
System.out.println(getBooleanType);
System.out.println(getBoolean);
if(getBooleanType != Boolean.class)
	throw new Exception("getBoolean not Boolean!");
callback("setBoolean",getBoolean);

System.out.println("getBooleanVector");
if(bindings.containsKey("getBooleanVector"))
	throw new Exception("getBooleanVector already defined!");
var getBooleanVector = callback("getBooleanVector");
var getBooleanVectorType = getBooleanVector.getClass().getComponentType();
System.out.println(getBooleanVectorType);
System.out.println(getBooleanVector);
if(getBooleanVectorType != boolean.class)
	throw new Exception("getBooleanVector not Boolean!");
callback("setBooleanVector",getBooleanVector);

System.out.println("getBooleanVectorAsList");
if(bindings.containsKey("getBooleanVectorAsList"))
	throw new Exception("getBooleanVectorAsList already defined!");
var getBooleanVectorAsList = callback("getBooleanVectorAsList");
var getBooleanVectorAsListType = getBooleanVectorAsList.getClass().getComponentType();
System.out.println(getBooleanVectorAsListType);
System.out.println(getBooleanVectorAsList);
if(getBooleanVectorAsListType != boolean.class)
	throw new Exception("getBooleanVectorAsList not Boolean!");
callback("setBooleanVectorAsList",getBooleanVectorAsList);

System.out.println("getBooleanMatrix");
if(bindings.containsKey("getBooleanMatrix"))
	throw new Exception("getBooleanMatrix already defined!");
var getBooleanMatrix = callback("getBooleanMatrix");
var getBooleanMatrixType = getBooleanMatrix[0].getClass().getComponentType();
System.out.println(getBooleanMatrixType);
System.out.println(getBooleanMatrix);
if(getBooleanMatrixType != boolean.class)
	throw new Exception("getBooleanMatrix not Boolean!");
callback("setBooleanMatrix",getBooleanMatrix);

System.out.println("getBooleanMatrixAsList");
if(bindings.containsKey("getBooleanMatrixAsList"))
	throw new Exception("getBooleanMatrixAsList already defined!");
var getBooleanMatrixAsList = callback("getBooleanMatrixAsList");
var getBooleanMatrixAsListType = getBooleanMatrixAsList[0].getClass().getComponentType();
System.out.println(getBooleanMatrixAsListType);
System.out.println(getBooleanMatrixAsList);
if(getBooleanMatrixAsListType != boolean.class)
	throw new Exception("getBooleanMatrixAsList not Boolean!");
callback("setBooleanMatrixAsList",getBooleanMatrixAsList);

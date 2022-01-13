System.out.println("getBoolean");
if(bindings.containsKey("getBoolean"))
	throw new Exception("getBoolean already defined!");
var getBoolean = putBoolean;
var getBooleanType = getBoolean.getClass();
System.out.println(getBooleanType);
System.out.println(getBoolean);
if(getBooleanType != Boolean.class)
	throw new Exception("getBoolean not Boolean!");

System.out.println("getBooleanVector");
if(bindings.containsKey("getBooleanVector"))
	throw new Exception("getBooleanVector already defined!");
var getBooleanVector = putBooleanVector;
var getBooleanVectorType = getBooleanVector.getClass().getComponentType();
System.out.println(getBooleanVectorType);
System.out.println(getBooleanVector);
if(getBooleanVectorType != boolean.class)
	throw new Exception("getBooleanVector not Boolean!");

System.out.println("getBooleanVectorAsList");
if(bindings.containsKey("getBooleanVectorAsList"))
	throw new Exception("getBooleanVectorAsList already defined!");
var getBooleanVectorAsList = putBooleanVectorAsList;
var getBooleanVectorAsListType = getBooleanVectorAsList.getClass().getComponentType();
System.out.println(getBooleanVectorAsListType);
System.out.println(getBooleanVectorAsList);
if(getBooleanVectorAsListType != boolean.class)
	throw new Exception("getBooleanVectorAsList not Boolean!");

System.out.println("getBooleanMatrix");
if(bindings.containsKey("getBooleanMatrix"))
	throw new Exception("getBooleanMatrix already defined!");
var getBooleanMatrix = putBooleanMatrix;
var getBooleanMatrixType = getBooleanMatrix[0].getClass().getComponentType();
System.out.println(getBooleanMatrixType);
System.out.println(getBooleanMatrix);
if(getBooleanMatrixType != boolean.class)
	throw new Exception("getBooleanMatrix not Boolean!");

System.out.println("getBooleanMatrixAsList");
if(bindings.containsKey("getBooleanMatrixAsList"))
	throw new Exception("getBooleanMatrixAsList already defined!");
var getBooleanMatrixAsList = putBooleanMatrixAsList;
var getBooleanMatrixAsListType = getBooleanMatrixAsList[0].getClass().getComponentType();
System.out.println(getBooleanMatrixAsListType);
System.out.println(getBooleanMatrixAsList);
if(getBooleanMatrixAsListType != boolean.class)
	throw new Exception("getBooleanMatrixAsList not Boolean!");

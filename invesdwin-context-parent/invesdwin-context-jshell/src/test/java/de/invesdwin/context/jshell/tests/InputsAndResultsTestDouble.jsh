System.out.println("getDouble");
if(bindings.containsKey("getDouble"))
	throw new Exception("getDouble already defined!");
var getDouble = putDouble;
var getDoubleType = getDouble.getClass();
System.out.println(getDoubleType);
System.out.println(getDouble);
if(getDoubleType != Double.class)
	throw new Exception("getDouble not Double!");

System.out.println("getDoubleVector");
if(bindings.containsKey("getDoubleVector"))
	throw new Exception("getDoubleVector already defined!");
var getDoubleVector = putDoubleVector;
var getDoubleVectorType = getDoubleVector.getClass().getComponentType();
System.out.println(getDoubleVectorType);
System.out.println(getDoubleVector);
if(getDoubleVectorType != double.class)
	throw new Exception("getDoubleVector not Double!");

System.out.println("getDoubleVectorAsList");
if(bindings.containsKey("getDoubleVectorAsList"))
	throw new Exception("getDoubleVectorAsList already defined!");
var getDoubleVectorAsList = putDoubleVectorAsList;
var getDoubleVectorAsListType = getDoubleVectorAsList.getClass().getComponentType();
System.out.println(getDoubleVectorAsListType);
System.out.println(getDoubleVectorAsList);
if(getDoubleVectorAsListType != double.class)
	throw new Exception("getDoubleVectorAsList not Double!");

System.out.println("getDoubleMatrix");
if(bindings.containsKey("getDoubleMatrix"))
	throw new Exception("getDoubleMatrix already defined!");
var getDoubleMatrix = putDoubleMatrix;
var getDoubleMatrixType = getDoubleMatrix[0].getClass().getComponentType();
System.out.println(getDoubleMatrixType);
System.out.println(getDoubleMatrix);
if(getDoubleMatrixType != double.class)
	throw new Exception("getDoubleMatrix not Double!");

System.out.println("getDoubleMatrixAsList");
if(bindings.containsKey("getDoubleMatrixAsList"))
	throw new Exception("getDoubleMatrixAsList already defined!");
var getDoubleMatrixAsList = putDoubleMatrixAsList;
var getDoubleMatrixAsListType = getDoubleMatrixAsList[0].getClass().getComponentType();
System.out.println(getDoubleMatrixAsListType);
System.out.println(getDoubleMatrixAsList);
if(getDoubleMatrixAsListType != double.class)
	throw new Exception("getDoubleMatrixAsList not Double!");

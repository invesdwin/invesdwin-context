System.out.println("getDouble");
if(binding.containsKey("getDouble"))
	throw new Exception("getDouble already defined!");
var getDouble = callback("getDouble");
var getDoubleType = getDouble.getClass();
System.out.println(getDoubleType);
System.out.println(getDouble);
if(getDoubleType != Double.class)
	throw new Exception("getDouble not Double!");
callback("setDouble",getDouble);

System.out.println("getDoubleVector");
if(binding.containsKey("getDoubleVector"))
	throw new Exception("getDoubleVector already defined!");
var getDoubleVector = callback("getDoubleVector");
var getDoubleVectorType = getDoubleVector.getClass().getComponentType();
System.out.println(getDoubleVectorType);
System.out.println(getDoubleVector);
if(getDoubleVectorType != double.class)
	throw new Exception("getDoubleVector not Double!");
callback("setDoubleVector",getDoubleVector);

System.out.println("getDoubleVectorAsList");
if(binding.containsKey("getDoubleVectorAsList"))
	throw new Exception("getDoubleVectorAsList already defined!");
var getDoubleVectorAsList = callback("getDoubleVectorAsList");
var getDoubleVectorAsListType = getDoubleVectorAsList.getClass().getComponentType();
System.out.println(getDoubleVectorAsListType);
System.out.println(getDoubleVectorAsList);
if(getDoubleVectorAsListType != double.class)
	throw new Exception("getDoubleVectorAsList not Double!");
callback("setDoubleVectorAsList",getDoubleVectorAsList);

System.out.println("getDoubleMatrix");
if(binding.containsKey("getDoubleMatrix"))
	throw new Exception("getDoubleMatrix already defined!");
var getDoubleMatrix = callback("getDoubleMatrix");
var getDoubleMatrixType = getDoubleMatrix[0].getClass().getComponentType();
System.out.println(getDoubleMatrixType);
System.out.println(getDoubleMatrix);
if(getDoubleMatrixType != double.class)
	throw new Exception("getDoubleMatrix not Double!");
callback("setDoubleMatrix",getDoubleMatrix);

System.out.println("getDoubleMatrixAsList");
if(binding.containsKey("getDoubleMatrixAsList"))
	throw new Exception("getDoubleMatrixAsList already defined!");
var getDoubleMatrixAsList = callback("getDoubleMatrixAsList");
var getDoubleMatrixAsListType = getDoubleMatrixAsList[0].getClass().getComponentType();
System.out.println(getDoubleMatrixAsListType);
System.out.println(getDoubleMatrixAsList);
if(getDoubleMatrixAsListType != double.class)
	throw new Exception("getDoubleMatrixAsList not Double!");
callback("setDoubleMatrixAsList",getDoubleMatrixAsList);

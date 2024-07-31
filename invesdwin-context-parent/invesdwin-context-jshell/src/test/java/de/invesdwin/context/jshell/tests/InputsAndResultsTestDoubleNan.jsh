System.out.println("getDouble");
if(binding.containsKey("getDouble"))
	throw new Exception("getDouble already defined!");
var getDouble = putDouble;
var getDoubleType = getDouble.getClass();
System.out.println(getDoubleType);
System.out.println(getDouble);
if(getDoubleType != Double.class)
	throw new Exception("getDouble not Double!");
if(!Double.isNaN(getDouble))
	throw new Exception("getDouble not NaN!");

System.out.println("getDoubleVector");
if(binding.containsKey("getDoubleVector"))
	throw new Exception("getDoubleVector already defined!");
var getDoubleVector = putDoubleVector;
var getDoubleVectorType = getDoubleVector.getClass().getComponentType();
System.out.println(getDoubleVectorType);
System.out.println(getDoubleVector);
if(getDoubleVectorType != double.class)
	throw new Exception("getDoubleVector not Double!");
if(!Double.isNaN(getDoubleVector[1]))
	throw new Exception("getDoubleVector[1] not NaN!");

System.out.println("getDoubleVectorAsList");
if(binding.containsKey("getDoubleVectorAsList"))
	throw new Exception("getDoubleVectorAsList already defined!");
var getDoubleVectorAsList = putDoubleVectorAsList;
var getDoubleVectorAsListType = getDoubleVectorAsList.getClass().getComponentType();
System.out.println(getDoubleVectorAsListType);
System.out.println(getDoubleVectorAsList);
if(getDoubleVectorAsListType != double.class)
	throw new Exception("getDoubleVectorAsList not Double!");
if(!Double.isNaN(getDoubleVectorAsList.get(1)))
	throw new Exception("getDoubleVectorAsList[1] not NaN!");

System.out.println("getDoubleMatrix");
if(binding.containsKey("getDoubleMatrix"))
	throw new Exception("getDoubleMatrix already defined!");
var getDoubleMatrix = putDoubleMatrix;
var getDoubleMatrixType = getDoubleMatrix[0].getClass().getComponentType();
System.out.println(getDoubleMatrixType);
System.out.println(getDoubleMatrix);
if(getDoubleMatrixType != double.class)
	throw new Exception("getDoubleMatrix not Double!");
if(!Double.isNaN(getDoubleMatrix[0][0]))
	throw new Exception("getDoubleMatrix[0][0] not NaN!");
if(!Double.isNaN(getDoubleMatrix[1][1]))
	throw new Exception("getDoubleMatrix[1][1] not NaN!");
if(!Double.isNaN(getDoubleMatrix[2][2]))
	throw new Exception("getDoubleMatrix[2][2] not NaN!");

System.out.println("getDoubleMatrixAsList");
if(binding.containsKey("getDoubleMatrixAsList"))
	throw new Exception("getDoubleMatrixAsList already defined!");
var getDoubleMatrixAsList = putDoubleMatrixAsList;
var getDoubleMatrixAsListType = getDoubleMatrixAsList[0].getClass().getComponentType();
System.out.println(getDoubleMatrixAsListType);
System.out.println(getDoubleMatrixAsList);
if(getDoubleMatrixAsListType != double.class)
	throw new Exception("getDoubleMatrixAsList not Double!");
if(!Double.isNaN(getDoubleMatrixAsList[0][0]))
	throw new Exception("getDoubleMatrixAsList[0][0] not NaN!");
if(!Double.isNaN(getDoubleMatrixAsList[1][1]))
	throw new Exception("getDoubleMatrixAsList[1][1] not NaN!");
if(!Double.isNaN(getDoubleMatrixAsList[2][2]))
	throw new Exception("getDoubleMatrixAsList[2][2] not NaN!");

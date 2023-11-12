System.out.println("getDecimal");
if(binding.containsKey("getDecimal"))
	throw new Exception("getDecimal already defined!");
var getDecimal = putDecimal;
var getDecimalType = getDecimal.getClass();
System.out.println(getDecimalType);
System.out.println(getDecimal);
if(getDecimalType != Double.class)
	throw new Exception("getDecimal not Double!");

System.out.println("getDecimalVector");
if(binding.containsKey("getDecimalVector"))
	throw new Exception("getDecimalVector already defined!");
var getDecimalVector = putDecimalVector;
var getDecimalVectorType = getDecimalVector.getClass().getComponentType();
System.out.println(getDecimalVectorType);
System.out.println(getDecimalVector);
if(getDecimalVectorType != double.class)
	throw new Exception("getDecimalVector not Double!");

System.out.println("getDecimalVectorAsList");
if(binding.containsKey("getDecimalVectorAsList"))
	throw new Exception("getDecimalVectorAsList already defined!");
var getDecimalVectorAsList = putDecimalVectorAsList;
var getDecimalVectorAsListType = getDecimalVectorAsList.getClass().getComponentType();
System.out.println(getDecimalVectorAsListType);
System.out.println(getDecimalVectorAsList);
if(getDecimalVectorAsListType != double.class)
	throw new Exception("getDecimalVectorAsList not Double!");

System.out.println("getDecimalMatrix");
if(binding.containsKey("getDecimalMatrix"))
	throw new Exception("getDecimalMatrix already defined!");
var getDecimalMatrix = putDecimalMatrix;
var getDecimalMatrixType = getDecimalMatrix[0].getClass().getComponentType();
System.out.println(getDecimalMatrixType);
System.out.println(getDecimalMatrix);
if(getDecimalMatrixType != double.class)
	throw new Exception("getDecimalMatrix not Double!");

System.out.println("getDecimalMatrixAsList");
if(binding.containsKey("getDecimalMatrixAsList"))
	throw new Exception("getDecimalMatrixAsList already defined!");
var getDecimalMatrixAsList = putDecimalMatrixAsList;
var getDecimalMatrixAsListType = getDecimalMatrixAsList[0].getClass().getComponentType();
System.out.println(getDecimalMatrixAsListType);
System.out.println(getDecimalMatrixAsList);
if(getDecimalMatrixAsListType != double.class)
	throw new Exception("getDecimalMatrixAsList not Double!");

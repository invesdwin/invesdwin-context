System.out.println("getInteger");
if(bindings.containsKey("getInteger"))
	throw new Exception("getInteger already defined!");
var getInteger = putInteger;
var getIntegerType = getInteger.getClass();
System.out.println(getIntegerType);
System.out.println(getInteger);
if(getIntegerType != Integer.class)
	throw new Exception("getInteger not Integer!");

System.out.println("getIntegerVector");
if(bindings.containsKey("getIntegerVector"))
	throw new Exception("getIntegerVector already defined!");
var getIntegerVector = putIntegerVector;
var getIntegerVectorType = getIntegerVector.getClass().getComponentType();
System.out.println(getIntegerVectorType);
System.out.println(getIntegerVector);
if(getIntegerVectorType != int.class)
	throw new Exception("getIntegerVector not Integer!");

System.out.println("getIntegerVectorAsList");
if(bindings.containsKey("getIntegerVectorAsList"))
	throw new Exception("getIntegerVectorAsList already defined!");
var getIntegerVectorAsList = putIntegerVectorAsList;
var getIntegerVectorAsListType = getIntegerVectorAsList.getClass().getComponentType();
System.out.println(getIntegerVectorAsListType);
System.out.println(getIntegerVectorAsList);
if(getIntegerVectorAsListType != int.class)
	throw new Exception("getIntegerVectorAsList not Integer!");

System.out.println("getIntegerMatrix");
if(bindings.containsKey("getIntegerMatrix"))
	throw new Exception("getIntegerMatrix already defined!");
var getIntegerMatrix = putIntegerMatrix;
var getIntegerMatrixType = getIntegerMatrix[0].getClass().getComponentType();
System.out.println(getIntegerMatrixType);
System.out.println(getIntegerMatrix);
if(getIntegerMatrixType != int.class)
	throw new Exception("getIntegerMatrix not Integer!");

System.out.println("getIntegerMatrixAsList");
if(bindings.containsKey("getIntegerMatrixAsList"))
	throw new Exception("getIntegerMatrixAsList already defined!");
var getIntegerMatrixAsList = putIntegerMatrixAsList;
var getIntegerMatrixAsListType = getIntegerMatrixAsList[0].getClass().getComponentType();
System.out.println(getIntegerMatrixAsListType);
System.out.println(getIntegerMatrixAsList);
if(getIntegerMatrixAsListType != int.class)
	throw new Exception("getIntegerMatrixAsList not Integer!");

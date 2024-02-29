System.out.println("getPercent");
if(binding.containsKey("getPercent"))
	throw new Exception("getPercent already defined!");
var getPercent = putPercent;
var getPercentType = getPercent.getClass();
System.out.println(getPercentType);
System.out.println(getPercent);
if(getPercentType != Double.class)
	throw new Exception("getPercent not Double!");

System.out.println("getPercentVector");
if(binding.containsKey("getPercentVector"))
	throw new Exception("getPercentVector already defined!");
var getPercentVector = putPercentVector;
var getPercentVectorType = getPercentVector.getClass().getComponentType();
System.out.println(getPercentVectorType);
System.out.println(getPercentVector);
if(getPercentVectorType != double.class)
	throw new Exception("getPercentVector not Double!");

System.out.println("getPercentVectorAsList");
if(binding.containsKey("getPercentVectorAsList"))
	throw new Exception("getPercentVectorAsList already defined!");
var getPercentVectorAsList = putPercentVectorAsList;
var getPercentVectorAsListType = getPercentVectorAsList.getClass().getComponentType();
System.out.println(getPercentVectorAsListType);
System.out.println(getPercentVectorAsList);
if(getPercentVectorAsListType != double.class)
	throw new Exception("getPercentVectorAsList not Double!");

System.out.println("getPercentMatrix");
if(binding.containsKey("getPercentMatrix"))
	throw new Exception("getPercentMatrix already defined!");
var getPercentMatrix = putPercentMatrix;
var getPercentMatrixType = getPercentMatrix[0].getClass().getComponentType();
System.out.println(getPercentMatrixType);
System.out.println(getPercentMatrix);
if(getPercentMatrixType != double.class)
	throw new Exception("getPercentMatrix not Double!");

System.out.println("getPercentMatrixAsList");
if(binding.containsKey("getPercentMatrixAsList"))
	throw new Exception("getPercentMatrixAsList already defined!");
var getPercentMatrixAsList = putPercentMatrixAsList;
var getPercentMatrixAsListType = getPercentMatrixAsList[0].getClass().getComponentType();
System.out.println(getPercentMatrixAsListType);
System.out.println(getPercentMatrixAsList);
if(getPercentMatrixAsListType != double.class)
	throw new Exception("getPercentMatrixAsList not Double!");

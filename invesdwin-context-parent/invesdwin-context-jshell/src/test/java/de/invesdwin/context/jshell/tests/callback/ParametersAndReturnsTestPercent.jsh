System.out.println("getPercent");
if(bindings.containsKey("getPercent"))
	throw new Exception("getPercent already defined!");
var getPercent = callback("getPercent");
var getPercentType = getPercent.getClass();
System.out.println(getPercentType);
System.out.println(getPercent);
if(getPercentType != Double.class)
	throw new Exception("getPercent not Double!");
callback("setPercent",getPercent);

System.out.println("getPercentVector");
if(bindings.containsKey("getPercentVector"))
	throw new Exception("getPercentVector already defined!");
var getPercentVector = callback("getPercentVector");
var getPercentVectorType = getPercentVector.getClass().getComponentType();
System.out.println(getPercentVectorType);
System.out.println(getPercentVector);
if(getPercentVectorType != double.class)
	throw new Exception("getPercentVector not Double!");
callback("setPercentVector",getPercentVector);

System.out.println("getPercentVectorAsList");
if(bindings.containsKey("getPercentVectorAsList"))
	throw new Exception("getPercentVectorAsList already defined!");
var getPercentVectorAsList = callback("getPercentVectorAsList");
var getPercentVectorAsListType = getPercentVectorAsList.getClass().getComponentType();
System.out.println(getPercentVectorAsListType);
System.out.println(getPercentVectorAsList);
if(getPercentVectorAsListType != double.class)
	throw new Exception("getPercentVectorAsList not Double!");
callback("setPercentVectorAsList",getPercentVectorAsList);

System.out.println("getPercentMatrix");
if(bindings.containsKey("getPercentMatrix"))
	throw new Exception("getPercentMatrix already defined!");
var getPercentMatrix = callback("getPercentMatrix");
var getPercentMatrixType = getPercentMatrix[0].getClass().getComponentType();
System.out.println(getPercentMatrixType);
System.out.println(getPercentMatrix);
if(getPercentMatrixType != double.class)
	throw new Exception("getPercentMatrix not Double!");
callback("setPercentMatrix",getPercentMatrix);

System.out.println("getPercentMatrixAsList");
if(bindings.containsKey("getPercentMatrixAsList"))
	throw new Exception("getPercentMatrixAsList already defined!");
var getPercentMatrixAsList = callback("getPercentMatrixAsList");
var getPercentMatrixAsListType = getPercentMatrixAsList[0].getClass().getComponentType();
System.out.println(getPercentMatrixAsListType);
System.out.println(getPercentMatrixAsList);
if(getPercentMatrixAsListType != double.class)
	throw new Exception("getPercentMatrixAsList not Double!");
callback("setPercentMatrixAsList",getPercentMatrixAsList);

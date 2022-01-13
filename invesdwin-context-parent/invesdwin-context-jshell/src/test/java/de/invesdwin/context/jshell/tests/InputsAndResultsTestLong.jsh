System.out.println("getLong");
if(bindings.containsKey("getLong"))
	throw new Exception("getLong already defined!");
var getLong = putLong;
var getLongType = getLong.getClass();
System.out.println(getLongType);
System.out.println(getLong);
if(getLongType != Long.class)
	throw new Exception("getLong not Long!");

System.out.println("getLongVector");
if(bindings.containsKey("getLongVector"))
	throw new Exception("getLongVector already defined!");
var getLongVector = putLongVector;
var getLongVectorType = getLongVector.getClass().getComponentType();
System.out.println(getLongVectorType);
System.out.println(getLongVector);
if(getLongVectorType != long.class)
	throw new Exception("getLongVector not Long!");

System.out.println("getLongVectorAsList");
if(bindings.containsKey("getLongVectorAsList"))
	throw new Exception("getLongVectorAsList already defined!");
var getLongVectorAsList = putLongVectorAsList;
var getLongVectorAsListType = getLongVectorAsList.getClass().getComponentType();
System.out.println(getLongVectorAsListType);
System.out.println(getLongVectorAsList);
if(getLongVectorAsListType != long.class)
	throw new Exception("getLongVectorAsList not Long!");

System.out.println("getLongMatrix");
if(bindings.containsKey("getLongMatrix"))
	throw new Exception("getLongMatrix already defined!");
var getLongMatrix = putLongMatrix;
var getLongMatrixType = getLongMatrix[0].getClass().getComponentType();
System.out.println(getLongMatrixType);
System.out.println(getLongMatrix);
if(getLongMatrixType != long.class)
	throw new Exception("getLongMatrix not Long!");

System.out.println("getLongMatrixAsList");
if(bindings.containsKey("getLongMatrixAsList"))
	throw new Exception("getLongMatrixAsList already defined!");
var getLongMatrixAsList = putLongMatrixAsList;
var getLongMatrixAsListType = getLongMatrixAsList[0].getClass().getComponentType();
System.out.println(getLongMatrixAsListType);
System.out.println(getLongMatrixAsList);
if(getLongMatrixAsListType != long.class)
	throw new Exception("getLongMatrixAsList not Long!");

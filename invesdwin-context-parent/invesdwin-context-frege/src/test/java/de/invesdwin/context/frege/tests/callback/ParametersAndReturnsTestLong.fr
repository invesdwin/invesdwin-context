System.out.println("getLong");
if(binding.containsKey("getLong"))
	throw new Exception("getLong already defined!");
var getLong = callback("getLong");
var getLongType = getLong.getClass();
System.out.println(getLongType);
System.out.println(getLong);
if(getLongType != Long.class)
	throw new Exception("getLong not Long!");
callback("setLong",getLong);

System.out.println("getLongVector");
if(binding.containsKey("getLongVector"))
	throw new Exception("getLongVector already defined!");
var getLongVector = callback("getLongVector");
var getLongVectorType = getLongVector.getClass().getComponentType();
System.out.println(getLongVectorType);
System.out.println(getLongVector);
if(getLongVectorType != long.class)
	throw new Exception("getLongVector not Long!");
callback("setLongVector",getLongVector);

System.out.println("getLongVectorAsList");
if(binding.containsKey("getLongVectorAsList"))
	throw new Exception("getLongVectorAsList already defined!");
var getLongVectorAsList = callback("getLongVectorAsList");
var getLongVectorAsListType = getLongVectorAsList.getClass().getComponentType();
System.out.println(getLongVectorAsListType);
System.out.println(getLongVectorAsList);
if(getLongVectorAsListType != long.class)
	throw new Exception("getLongVectorAsList not Long!");
callback("setLongVectorAsList",getLongVectorAsList);

System.out.println("getLongMatrix");
if(binding.containsKey("getLongMatrix"))
	throw new Exception("getLongMatrix already defined!");
var getLongMatrix = callback("getLongMatrix");
var getLongMatrixType = getLongMatrix[0].getClass().getComponentType();
System.out.println(getLongMatrixType);
System.out.println(getLongMatrix);
if(getLongMatrixType != long.class)
	throw new Exception("getLongMatrix not Long!");
callback("setLongMatrix",getLongMatrix);

System.out.println("getLongMatrixAsList");
if(binding.containsKey("getLongMatrixAsList"))
	throw new Exception("getLongMatrixAsList already defined!");
var getLongMatrixAsList = callback("getLongMatrixAsList");
var getLongMatrixAsListType = getLongMatrixAsList[0].getClass().getComponentType();
System.out.println(getLongMatrixAsListType);
System.out.println(getLongMatrixAsList);
if(getLongMatrixAsListType != long.class)
	throw new Exception("getLongMatrixAsList not Long!");
callback("setLongMatrixAsList",getLongMatrixAsList);

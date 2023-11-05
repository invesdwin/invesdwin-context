System.out.println("getShort");
if(bindings.containsKey("getShort"))
	throw new Exception("getShort already defined!");
var getShort = callback("getShort");
var getShortType = getShort.getClass();
System.out.println(getShortType);
System.out.println(getShort);
if(getShortType != Short.class)
	throw new Exception("getShort not Short!");
callback("setShort",getShort);

System.out.println("getShortVector");
if(bindings.containsKey("getShortVector"))
	throw new Exception("getShortVector already defined!");
var getShortVector = callback("getShortVector");
var getShortVectorType = getShortVector.getClass().getComponentType();
System.out.println(getShortVectorType);
System.out.println(getShortVector);
if(getShortVectorType != short.class)
	throw new Exception("getShortVector not Short!");
callback("setShortVector",getShortVector);

System.out.println("getShortVectorAsList");
if(bindings.containsKey("getShortVectorAsList"))
	throw new Exception("getShortVectorAsList already defined!");
var getShortVectorAsList = callback("getShortVectorAsList");
var getShortVectorAsListType = getShortVectorAsList.getClass().getComponentType();
System.out.println(getShortVectorAsListType);
System.out.println(getShortVectorAsList);
if(getShortVectorAsListType != short.class)
	throw new Exception("getShortVectorAsList not Short!");
callback("setShortVectorAsList",getShortVectorAsList);

System.out.println("getShortMatrix");
if(bindings.containsKey("getShortMatrix"))
	throw new Exception("getShortMatrix already defined!");
var getShortMatrix = callback("getShortMatrix");
var getShortMatrixType = getShortMatrix[0].getClass().getComponentType();
System.out.println(getShortMatrixType);
System.out.println(getShortMatrix);
if(getShortMatrixType != short.class)
	throw new Exception("getShortMatrix not Short!");
callback("setShortMatrix",getShortMatrix);

System.out.println("getShortMatrixAsList");
if(bindings.containsKey("getShortMatrixAsList"))
	throw new Exception("getShortMatrixAsList already defined!");
var getShortMatrixAsList = callback("getShortMatrixAsList");
var getShortMatrixAsListType = getShortMatrixAsList[0].getClass().getComponentType();
System.out.println(getShortMatrixAsListType);
System.out.println(getShortMatrixAsList);
if(getShortMatrixAsListType != short.class)
	throw new Exception("getShortMatrixAsList not Short!");
callback("setShortMatrixAsList",getShortMatrixAsList);

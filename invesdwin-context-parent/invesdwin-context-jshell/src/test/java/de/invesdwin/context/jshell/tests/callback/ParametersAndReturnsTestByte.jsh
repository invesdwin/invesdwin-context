System.out.println("getByte");
if(bindings.containsKey("getByte"))
	throw new Exception("getByte already defined!");
var getByte = callback("getByte");
var getByteType = getByte.getClass();
System.out.println(getByteType);
System.out.println(getByte);
if(getByteType != Byte.class)
	throw new Exception("getByte not Byte!");
callback("setByte",getByte);

System.out.println("getByteVector");
if(bindings.containsKey("getByteVector"))
	throw new Exception("getByteVector already defined!");
var getByteVector = callback("getByteVector");
var getByteVectorType = getByteVector.getClass().getComponentType();
System.out.println(getByteVectorType);
System.out.println(getByteVector);
if(getByteVectorType != byte.class)
	throw new Exception("getByteVector not Byte!");
callback("setByteVector",getByteVector);

System.out.println("getByteVectorAsList");
if(bindings.containsKey("getByteVectorAsList"))
	throw new Exception("getByteVectorAsList already defined!");
var getByteVectorAsList = callback("getByteVectorAsList");
var getByteVectorAsListType = getByteVectorAsList.getClass().getComponentType();
System.out.println(getByteVectorAsListType);
System.out.println(getByteVectorAsList);
if(getByteVectorAsListType != byte.class)
	throw new Exception("getByteVectorAsList not Byte!");
callback("setByteVectorAsList",getByteVectorAsList);

System.out.println("getByteMatrix");
if(bindings.containsKey("getByteMatrix"))
	throw new Exception("getByteMatrix already defined!");
var getByteMatrix = callback("getByteMatrix");
var getByteMatrixType = getByteMatrix[0].getClass().getComponentType();
System.out.println(getByteMatrixType);
System.out.println(getByteMatrix);
if(getByteMatrixType != byte.class)
	throw new Exception("getByteMatrix not Byte!");
callback("setByteMatrix",getByteMatrix);

System.out.println("getByteMatrixAsList");
if(bindings.containsKey("getByteMatrixAsList"))
	throw new Exception("getByteMatrixAsList already defined!");
var getByteMatrixAsList = callback("getByteMatrixAsList");
var getByteMatrixAsListType = getByteMatrixAsList[0].getClass().getComponentType();
System.out.println(getByteMatrixAsListType);
System.out.println(getByteMatrixAsList);
if(getByteMatrixAsListType != byte.class)
	throw new Exception("getByteMatrixAsList not Byte!");
callback("setByteMatrixAsList",getByteMatrixAsList);

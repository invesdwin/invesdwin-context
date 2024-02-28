System.out.println("getString");
if(binding.containsKey("getString"))
	throw new Exception("getString already defined!");
var getString = callback("getString");
var getStringType = getString.getClass();
System.out.println(getStringType);
System.out.println(getString);
if(getStringType != String.class)
	throw new Exception("getString not String!");
callback("setString",getString);

System.out.println("getStringWithNull");
if(binding.containsKey("getStringWithNull"))
	throw new Exception("getStringWithNull already defined!");
var getStringWithNull = callback("getStringWithNull");
System.out.println(getStringWithNull);
if(getStringWithNull != null)
	throw new Exception("getStringWithNull not null!");
callback("setStringWithNull",getStringWithNull);

System.out.println("getStringVector");
if(binding.containsKey("getStringVector"))
	throw new Exception("getStringVector already defined!");
var getStringVector = callback("getStringVector");
var getStringVectorType = getStringVector.getClass().getComponentType();
System.out.println(getStringVectorType);
System.out.println(getStringVector);
if(getStringVectorType != String.class)
	throw new Exception("getStringVector not String!");
callback("setStringVector",getStringVector);

System.out.println("getStringVectorWithNull");
if(binding.containsKey("getStringVectorWithNull"))
	throw new Exception("getStringVectorWithNull already defined!");
var getStringVectorWithNull = callback("getStringVectorWithNull");
var getStringVectorWithNullType = getStringVectorWithNull.getClass().getComponentType();
System.out.println(getStringVectorWithNullType);
System.out.println(getStringVectorWithNull);
if(getStringVectorWithNullType != String.class)
	throw new Exception("getStringVectorWithNull not String!");
if(getStringVectorWithNull[1] != null)
	throw new Exception("getStringVectorWithNull[1] not null!");
callback("setStringVectorWithNull",getStringVectorWithNull);

System.out.println("getStringVectorAsList");
if(binding.containsKey("getStringVectorAsList"))
	throw new Exception("getStringVectorAsList already defined!");
var getStringVectorAsList = callback("getStringVectorAsList");
var getStringVectorAsListType = getStringVectorAsList.getClass().getComponentType();
System.out.println(getStringVectorAsListType);
System.out.println(getStringVectorAsList);
if(getStringVectorAsListType != String.class)
	throw new Exception("getStringVectorAsList not String!");
callback("setStringVectorAsList",getStringVectorAsList);

System.out.println("getStringVectorAsListWithNull");
if(binding.containsKey("getStringVectorAsListWithNull"))
	throw new Exception("getStringVectorAsListWithNull already defined!");
var getStringVectorAsListWithNull = callback("getStringVectorAsListWithNull");
var getStringVectorAsListWithNullType = getStringVectorAsListWithNull.getClass().getComponentType();
System.out.println(getStringVectorAsListWithNullType);
System.out.println(getStringVectorAsListWithNull);
if(getStringVectorAsListWithNullType != String.class)
	throw new Exception("getStringVectorAsListWithNull not String!");
if(getStringVectorAsListWithNull[1] != null)
	throw new Exception("getStringVectorAsListWithNull[1] not null!");
callback("setStringVectorAsListWithNull",getStringVectorAsListWithNull);

System.out.println("getStringMatrix");
if(binding.containsKey("getStringMatrix"))
	throw new Exception("getStringMatrix already defined!");
var getStringMatrix = callback("getStringMatrix");
var getStringMatrixType = getStringMatrix[0].getClass().getComponentType();
System.out.println(getStringMatrixType);
System.out.println(getStringMatrix);
if(getStringMatrixType != String.class)
	throw new Exception("getStringMatrix not String!");
callback("setStringMatrix",getStringMatrix);

System.out.println("getStringMatrixWithNull");
if(binding.containsKey("getStringMatrixWithNull"))
	throw new Exception("getStringMatrixWithNull already defined!");
var getStringMatrixWithNull = callback("getStringMatrixWithNull");
var getStringMatrixWithNullType = getStringMatrixWithNull[0][1].getClass();
System.out.println(getStringMatrixWithNullType);
System.out.println(getStringMatrixWithNull);
if(getStringMatrixWithNullType != String.class)
	throw new Exception("getStringMatrixWithNull not String!");
if(getStringMatrixWithNull[0][0] != null)
	throw new Exception("getStringMatrixWithNull[0][0] not null!");
if(getStringMatrixWithNull[1][1] != null)
	throw new Exception("getStringMatrixWithNull[1][1] not null!");
if(getStringMatrixWithNull[2][2] != null)
	throw new Exception("getStringMatrixWithNull[2][2] not null!");
callback("setStringMatrixWithNull",getStringMatrixWithNull);

System.out.println("getStringMatrixAsList");
if(binding.containsKey("getStringMatrixAsList"))
	throw new Exception("getStringMatrixAsList already defined!");
var getStringMatrixAsList = callback("getStringMatrixAsList");
var getStringMatrixAsListType = getStringMatrixAsList[0].getClass().getComponentType();
System.out.println(getStringMatrixAsListType);
System.out.println(getStringMatrixAsList);
if(getStringMatrixAsListType != String.class)
	throw new Exception("getStringMatrixAsList not String!");
callback("setStringMatrixAsList",getStringMatrixAsList);

System.out.println("getStringMatrixAsListWithNull");
if(binding.containsKey("getStringMatrixAsListWithNull"))
	throw new Exception("getStringMatrixAsListWithNull already defined!");
var getStringMatrixAsListWithNull = callback("getStringMatrixAsListWithNull");
var getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1].getClass();
System.out.println(getStringMatrixAsListWithNullType);
System.out.println(getStringMatrixAsListWithNull);
if(getStringMatrixAsListWithNullType != String.class)
	throw new Exception("getStringMatrixAsListWithNull not String!");
if(getStringMatrixAsListWithNull[0][0] != null)
	throw new Exception("getStringMatrixAsListWithNull[0][0] not null!");
if(getStringMatrixAsListWithNull[1][1] != null)
	throw new Exception("getStringMatrixAsListWithNull[1][1] not null!");
if(getStringMatrixAsListWithNull[2][2] != null)
	throw new Exception("getStringMatrixAsListWithNull[2][2] not null!");
callback("setStringMatrixAsListWithNull",getStringMatrixAsListWithNull);

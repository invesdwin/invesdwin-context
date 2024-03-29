System.out.println("getString")
if(getString != null)
	throw new Exception("getString already defined!")
val getString = putString
val getStringType = getString.getClass()
System.out.println(getStringType)
System.out.println(getString)
if(getStringType != classOf[java.lang.String])
	throw new Exception("getString not String!")

System.out.println("getStringWithNull")
if(getStringWithNull != null)
	throw new Exception("getStringWithNull already defined!")
val getStringWithNull = putStringWithNull
System.out.println(getStringWithNull)
if(getStringWithNull != null)
	throw new Exception("getStringWithNull not null!")

System.out.println("getStringVector")
if(getStringVector != null)
	throw new Exception("getStringVector already defined!")
val getStringVector = putStringVector.asInstanceOf[Array[String]]
val getStringVectorType = getStringVector(0).getClass()
System.out.println(getStringVectorType)
System.out.println(getStringVector)
if(getStringVectorType != classOf[java.lang.String])
	throw new Exception("getStringVector not String!")

System.out.println("getStringVectorWithNull")
if(getStringVectorWithNull != null)
	throw new Exception("getStringVectorWithNull already defined!")
val getStringVectorWithNull = putStringVectorWithNull.asInstanceOf[Array[String]]
val getStringVectorWithNullType = getStringVectorWithNull(0).getClass()
System.out.println(getStringVectorWithNullType)
System.out.println(getStringVectorWithNull)
if(getStringVectorWithNullType != classOf[java.lang.String])
	throw new Exception("getStringVectorWithNull not String!")
if(getStringVectorWithNull(1) != null)
	throw new Exception("getStringVectorWithNull(1) not null!")

System.out.println("getStringVectorAsList")
if(getStringVectorAsList != null)
	throw new Exception("getStringVectorAsList already defined!")
val getStringVectorAsList = putStringVectorAsList.asInstanceOf[Array[String]]
val getStringVectorAsListType = getStringVectorAsList(0).getClass()
System.out.println(getStringVectorAsListType)
System.out.println(getStringVectorAsList)
if(getStringVectorAsListType != classOf[java.lang.String])
	throw new Exception("getStringVectorAsList not String!")

System.out.println("getStringVectorAsListWithNull")
if(getStringVectorAsListWithNull != null)
	throw new Exception("getStringVectorAsListWithNull already defined!")
val getStringVectorAsListWithNull = putStringVectorAsListWithNull.asInstanceOf[Array[String]]
val getStringVectorAsListWithNullType = getStringVectorAsListWithNull(0).getClass()
System.out.println(getStringVectorAsListWithNullType)
System.out.println(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType != classOf[java.lang.String])
	throw new Exception("getStringVectorAsListWithNull not String!")
if(getStringVectorAsListWithNull(1) != null)
	throw new Exception("getStringVectorAsListWithNull(1) not null!")

System.out.println("getStringMatrix")
if(getStringMatrix != null)
	throw new Exception("getStringMatrix already defined!")
val getStringMatrix = putStringMatrix.asInstanceOf[Array[Array[String]]]
val getStringMatrixType = getStringMatrix(0)(0).getClass()
System.out.println(getStringMatrixType)
System.out.println(getStringMatrix)
if(getStringMatrixType != classOf[java.lang.String])
	throw new Exception("getStringMatrix not String!")

System.out.println("getStringMatrixWithNull")
if(getStringMatrixWithNull != null)
	throw new Exception("getStringMatrixWithNull already defined!")
val getStringMatrixWithNull = putStringMatrixWithNull.asInstanceOf[Array[Array[String]]]
val getStringMatrixWithNullType = getStringMatrixWithNull(0)(1).getClass()
System.out.println(getStringMatrixWithNullType)
System.out.println(getStringMatrixWithNull)
if(getStringMatrixWithNullType != classOf[java.lang.String])
	throw new Exception("getStringMatrixWithNull not String!")
if(getStringMatrixWithNull(0)(0) != null)
	throw new Exception("getStringMatrixWithNull(0)(0) not null!")
if(getStringMatrixWithNull(1)(1) != null)
	throw new Exception("getStringMatrixWithNull(1)(1) not null!")
if(getStringMatrixWithNull(2)(2) != null)
	throw new Exception("getStringMatrixWithNull(2)(2) not null!")

System.out.println("getStringMatrixAsList")
if(getStringMatrixAsList != null)
	throw new Exception("getStringMatrixAsList already defined!")
val getStringMatrixAsList = putStringMatrixAsList.asInstanceOf[Array[Array[String]]]
val getStringMatrixAsListType = getStringMatrixAsList(0)(0).getClass()
System.out.println(getStringMatrixAsListType)
System.out.println(getStringMatrixAsList)
if(getStringMatrixAsListType != classOf[java.lang.String])
	throw new Exception("getStringMatrixAsList not String!")

System.out.println("getStringMatrixAsListWithNull")
if(getStringMatrixAsListWithNull != null)
	throw new Exception("getStringMatrixAsListWithNull already defined!")
val getStringMatrixAsListWithNull = putStringMatrixAsListWithNull.asInstanceOf[Array[Array[String]]]
val getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull(0)(1).getClass()
System.out.println(getStringMatrixAsListWithNullType)
System.out.println(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType != classOf[java.lang.String])
	throw new Exception("getStringMatrixAsListWithNull not String!")
if(getStringMatrixAsListWithNull(0)(0) != null)
	throw new Exception("getStringMatrixAsListWithNull(0)(0) not null!")
if(getStringMatrixAsListWithNull(1)(1) != null)
	throw new Exception("getStringMatrixAsListWithNull(1)(1) not null!")
if(getStringMatrixAsListWithNull(2)(2) != null)
	throw new Exception("getStringMatrixAsListWithNull(2)(2) not null!")

System.out.println("getFloat")
if(getFloat != null)
	throw new Exception("getFloat already defined!")
val getFloat: java.lang.Float = callback("getFloat")
val getFloatType = getFloat.getClass()
System.out.println(getFloatType)
System.out.println(getFloat)
if(getFloatType != classOf[java.lang.Float])
	throw new Exception("getFloat not Float!")
callback("setFloat",getFloat)

System.out.println("getFloatVector")
if(getFloatVector != null)
	throw new Exception("getFloatVector already defined!")
val getFloatVector: Array[Float] = callback("getFloatVector")
val getFloatVectorType = getFloatVector(0).getClass()
System.out.println(getFloatVectorType)
System.out.println(getFloatVector)
if(getFloatVectorType != classOf[Float])
	throw new Exception("getFloatVector not Float!")
callback("setFloatVector",getFloatVector)

System.out.println("getFloatVectorAsList")
if(getFloatVectorAsList != null)
	throw new Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList: Array[Float] = callback("getFloatVectorAsList")
val getFloatVectorAsListType = getFloatVectorAsList(0).getClass()
System.out.println(getFloatVectorAsListType)
System.out.println(getFloatVectorAsList)
if(getFloatVectorAsListType != classOf[Float])
	throw new Exception("getFloatVectorAsList not Float!")
callback("setFloatVectorAsList",getFloatVectorAsList)

System.out.println("getFloatMatrix")
if(getFloatMatrix != null)
	throw new Exception("getFloatMatrix already defined!")
val getFloatMatrix: Array[Array[Float]] = callback("getFloatMatrix")
val getFloatMatrixType = getFloatMatrix(0)(0).getClass()
System.out.println(getFloatMatrixType)
System.out.println(getFloatMatrix)
if(getFloatMatrixType != classOf[Float])
	throw new Exception("getFloatMatrix not Float!")
callback("setFloatMatrix",getFloatMatrix)

System.out.println("getFloatMatrixAsList")
if(getFloatMatrixAsList != null)
	throw new Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList: Array[Array[Float]] = callback("getFloatMatrixAsList")
val getFloatMatrixAsListType = getFloatMatrixAsList(0)(0).getClass()
System.out.println(getFloatMatrixAsListType)
System.out.println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != classOf[Float])
	throw new Exception("getFloatMatrixAsList not Float!")
callback("setFloatMatrixAsList",getFloatMatrixAsList)

System.out.println("getFloat")
if(getFloat != null)
	throw new Exception("getFloat already defined!")
val getFloat = putFloat
val getFloatType = getFloat.getClass()
System.out.println(getFloatType)
System.out.println(getFloat)
if(getFloatType != classOf[java.lang.Float])
	throw new Exception("getFloat not Float!")

System.out.println("getFloatVector")
if(getFloatVector != null)
	throw new Exception("getFloatVector already defined!")
val getFloatVector = putFloatVector.asInstanceOf[Array[Float]]
val getFloatVectorType = getFloatVector(0).getClass()
System.out.println(getFloatVectorType)
System.out.println(getFloatVector)
if(getFloatVectorType != classOf[Float])
	throw new Exception("getFloatVector not Float!")

System.out.println("getFloatVectorAsList")
if(getFloatVectorAsList != null)
	throw new Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList = putFloatVectorAsList.asInstanceOf[Array[Float]]
val getFloatVectorAsListType = getFloatVectorAsList(0).getClass()
System.out.println(getFloatVectorAsListType)
System.out.println(getFloatVectorAsList)
if(getFloatVectorAsListType != classOf[Float])
	throw new Exception("getFloatVectorAsList not Float!")

System.out.println("getFloatMatrix")
if(getFloatMatrix != null)
	throw new Exception("getFloatMatrix already defined!")
val getFloatMatrix = putFloatMatrix.asInstanceOf[Array[Array[Float]]]
val getFloatMatrixType = getFloatMatrix(0)(0).getClass()
System.out.println(getFloatMatrixType)
System.out.println(getFloatMatrix)
if(getFloatMatrixType != classOf[Float])
	throw new Exception("getFloatMatrix not Float!")

System.out.println("getFloatMatrixAsList")
if(getFloatMatrixAsList != null)
	throw new Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList = putFloatMatrixAsList.asInstanceOf[Array[Array[Float]]]
val getFloatMatrixAsListType = getFloatMatrixAsList(0)(0).getClass()
System.out.println(getFloatMatrixAsListType)
System.out.println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != classOf[Float])
	throw new Exception("getFloatMatrixAsList not Float!")

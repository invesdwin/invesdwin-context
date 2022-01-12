println("getFloat")
if(getFloat != null)
	throw new Exception("getFloat already defined!")
val getFloat = putFloat
val getFloatType = getFloat.getClass()
println(getFloatType)
println(getFloat)
if(getFloatType != classOf[java.lang.Float])
	throw new Exception("getFloat not Float!")

println("getFloatVector")
if(getFloatVector != null)
	throw new Exception("getFloatVector already defined!")
val getFloatVector = putFloatVector.asInstanceOf[Array[Float]]
val getFloatVectorType = getFloatVector(0).getClass()
println(getFloatVectorType)
println(getFloatVector)
if(getFloatVectorType != classOf[Float])
	throw new Exception("getFloatVector not Float!")

println("getFloatVectorAsList")
if(getFloatVectorAsList != null)
	throw new Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList = putFloatVectorAsList.asInstanceOf[Array[Float]]
val getFloatVectorAsListType = getFloatVectorAsList(0).getClass()
println(getFloatVectorAsListType)
println(getFloatVectorAsList)
if(getFloatVectorAsListType != classOf[Float])
	throw new Exception("getFloatVectorAsList not Float!")

println("getFloatMatrix")
if(getFloatMatrix != null)
	throw new Exception("getFloatMatrix already defined!")
val getFloatMatrix = putFloatMatrix.asInstanceOf[Array[Array[Float]]]
val getFloatMatrixType = getFloatMatrix(0)(0).getClass()
println(getFloatMatrixType)
println(getFloatMatrix)
if(getFloatMatrixType != classOf[Float])
	throw new Exception("getFloatMatrix not Float!")

println("getFloatMatrixAsList")
if(getFloatMatrixAsList != null)
	throw new Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList = putFloatMatrixAsList.asInstanceOf[Array[Array[Float]]]
val getFloatMatrixAsListType = getFloatMatrixAsList(0)(0).getClass()
println(getFloatMatrixAsListType)
println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != classOf[Float])
	throw new Exception("getFloatMatrixAsList not Float!")

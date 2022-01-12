println("getBoolean")
if(getBoolean != null)
	throw new Exception("getBoolean already defined!")
val getBoolean = putBoolean
val getBooleanType = getBoolean.getClass()
println(getBooleanType)
println(getBoolean)
if(getBooleanType != classOf[java.lang.Boolean])
	throw new Exception("getBoolean not Boolean!")

println("getBooleanVector")
if(getBooleanVector != null)
	throw new Exception("getBooleanVector already defined!")
val getBooleanVector = putBooleanVector.asInstanceOf[Array[Boolean]]
val getBooleanVectorType = getBooleanVector(0).getClass()
println(getBooleanVectorType)
println(getBooleanVector)
if(getBooleanVectorType != classOf[Boolean])
	throw new Exception("getBooleanVector not Boolean!")

println("getBooleanVectorAsList")
if(getBooleanVectorAsList != null)
	throw new Exception("getBooleanVectorAsList already defined!")
val getBooleanVectorAsList = putBooleanVectorAsList.asInstanceOf[Array[Boolean]]
val getBooleanVectorAsListType = getBooleanVectorAsList(0).getClass()
println(getBooleanVectorAsListType)
println(getBooleanVectorAsList)
if(getBooleanVectorAsListType != classOf[Boolean])
	throw new Exception("getBooleanVectorAsList not Boolean!")

println("getBooleanMatrix")
if(getBooleanMatrix != null)
	throw new Exception("getBooleanMatrix already defined!")
val getBooleanMatrix = putBooleanMatrix.asInstanceOf[Array[Array[Boolean]]]
val getBooleanMatrixType = getBooleanMatrix(0)(0).getClass()
println(getBooleanMatrixType)
println(getBooleanMatrix)
if(getBooleanMatrixType != classOf[Boolean])
	throw new Exception("getBooleanMatrix not Boolean!")

println("getBooleanMatrixAsList")
if(getBooleanMatrixAsList != null)
	throw new Exception("getBooleanMatrixAsList already defined!")
val getBooleanMatrixAsList = putBooleanMatrixAsList.asInstanceOf[Array[Array[Boolean]]]
val getBooleanMatrixAsListType = getBooleanMatrixAsList(0)(0).getClass()
println(getBooleanMatrixAsListType)
println(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != classOf[Boolean])
	throw new Exception("getBooleanMatrixAsList not Boolean!")

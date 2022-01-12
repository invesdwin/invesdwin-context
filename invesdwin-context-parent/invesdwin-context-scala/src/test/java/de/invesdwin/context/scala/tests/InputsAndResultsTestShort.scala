println("getShort")
if(getShort != null)
	throw new Exception("getShort already defined!")
val getShort = putShort
val getShortType = getShort.getClass()
println(getShortType)
println(getShort)
if(getShortType != classOf[java.lang.Short])
	throw new Exception("getShort not Short!")

println("getShortVector")
if(getShortVector != null)
	throw new Exception("getShortVector already defined!")
val getShortVector = putShortVector.asInstanceOf[Array[Short]]
val getShortVectorType = getShortVector(0).getClass()
println(getShortVectorType)
println(getShortVector)
if(getShortVectorType != classOf[Short])
	throw new Exception("getShortVector not Short!")

println("getShortVectorAsList")
if(getShortVectorAsList != null)
	throw new Exception("getShortVectorAsList already defined!")
val getShortVectorAsList = putShortVectorAsList.asInstanceOf[Array[Short]]
val getShortVectorAsListType = getShortVectorAsList(0).getClass()
println(getShortVectorAsListType)
println(getShortVectorAsList)
if(getShortVectorAsListType != classOf[Short])
	throw new Exception("getShortVectorAsList not Short!")

println("getShortMatrix")
if(getShortMatrix != null)
	throw new Exception("getShortMatrix already defined!")
val getShortMatrix = putShortMatrix.asInstanceOf[Array[Array[Short]]]
val getShortMatrixType = getShortMatrix(0)(0).getClass()
println(getShortMatrixType)
println(getShortMatrix)
if(getShortMatrixType != classOf[Short])
	throw new Exception("getShortMatrix not Short!")

println("getShortMatrixAsList")
if(getShortMatrixAsList != null)
	throw new Exception("getShortMatrixAsList already defined!")
val getShortMatrixAsList = putShortMatrixAsList.asInstanceOf[Array[Array[Short]]]
val getShortMatrixAsListType = getShortMatrixAsList(0)(0).getClass()
println(getShortMatrixAsListType)
println(getShortMatrixAsList)
if(getShortMatrixAsListType != classOf[Short])
	throw new Exception("getShortMatrixAsList not Short!")

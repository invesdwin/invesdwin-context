println("getDouble")
if(getDouble != null)
	throw new Exception("getDouble already defined!")
val getDouble = putDouble
val getDoubleType = getDouble.getClass()
println(getDoubleType)
println(getDouble)
if(getDoubleType != classOf[java.lang.Double])
	throw new Exception("getDouble not Double!")

println("getDoubleVector")
if(getDoubleVector != null)
	throw new Exception("getDoubleVector already defined!")
val getDoubleVector = putDoubleVector.asInstanceOf[Array[Double]]
val getDoubleVectorType = getDoubleVector(0).getClass()
println(getDoubleVectorType)
println(getDoubleVector)
if(getDoubleVectorType != classOf[Double])
	throw new Exception("getDoubleVector not Double!")

println("getDoubleVectorAsList")
if(getDoubleVectorAsList != null)
	throw new Exception("getDoubleVectorAsList already defined!")
val getDoubleVectorAsList = putDoubleVectorAsList.asInstanceOf[Array[Double]]
val getDoubleVectorAsListType = getDoubleVectorAsList(0).getClass()
println(getDoubleVectorAsListType)
println(getDoubleVectorAsList)
if(getDoubleVectorAsListType != classOf[Double])
	throw new Exception("getDoubleVectorAsList not Double!")

println("getDoubleMatrix")
if(getDoubleMatrix != null)
	throw new Exception("getDoubleMatrix already defined!")
val getDoubleMatrix = putDoubleMatrix.asInstanceOf[Array[Array[Double]]]
val getDoubleMatrixType = getDoubleMatrix(0)(0).getClass()
println(getDoubleMatrixType)
println(getDoubleMatrix)
if(getDoubleMatrixType != classOf[Double])
	throw new Exception("getDoubleMatrix not Double!")

println("getDoubleMatrixAsList")
if(getDoubleMatrixAsList != null)
	throw new Exception("getDoubleMatrixAsList already defined!")
val getDoubleMatrixAsList = putDoubleMatrixAsList.asInstanceOf[Array[Array[Double]]]
val getDoubleMatrixAsListType = getDoubleMatrixAsList(0)(0).getClass()
println(getDoubleMatrixAsListType)
println(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != classOf[Double])
	throw new Exception("getDoubleMatrixAsList not Double!")

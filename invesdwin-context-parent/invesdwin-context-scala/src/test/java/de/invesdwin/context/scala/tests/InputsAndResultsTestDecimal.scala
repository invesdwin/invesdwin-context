println("getDecimal")
if(getDecimal != null)
	throw new Exception("getDecimal already defined!")
val getDecimal = putDecimal
val getDecimalType = getDecimal.getClass()
println(getDecimalType)
println(getDecimal)
if(getDecimalType != classOf[java.lang.Double])
	throw new Exception("getDecimal not Double!")

println("getDecimalVector")
if(getDecimalVector != null)
	throw new Exception("getDecimalVector already defined!")
val getDecimalVector = putDecimalVector.asInstanceOf[Array[Double]]
val getDecimalVectorType = getDecimalVector(0).getClass()
println(getDecimalVectorType)
println(getDecimalVector)
if(getDecimalVectorType != classOf[Double])
	throw new Exception("getDecimalVector not Double!")

println("getDecimalVectorAsList")
if(getDecimalVectorAsList != null)
	throw new Exception("getDecimalVectorAsList already defined!")
val getDecimalVectorAsList = putDecimalVectorAsList.asInstanceOf[Array[Double]]
val getDecimalVectorAsListType = getDecimalVectorAsList(0).getClass()
println(getDecimalVectorAsListType)
println(getDecimalVectorAsList)
if(getDecimalVectorAsListType != classOf[Double])
	throw new Exception("getDecimalVectorAsList not Double!")

println("getDecimalMatrix")
if(getDecimalMatrix != null)
	throw new Exception("getDecimalMatrix already defined!")
val getDecimalMatrix = putDecimalMatrix.asInstanceOf[Array[Array[Double]]]
val getDecimalMatrixType = getDecimalMatrix(0)(0).getClass()
println(getDecimalMatrixType)
println(getDecimalMatrix)
if(getDecimalMatrixType != classOf[Double])
	throw new Exception("getDecimalMatrix not Double!")

println("getDecimalMatrixAsList")
if(getDecimalMatrixAsList != null)
	throw new Exception("getDecimalMatrixAsList already defined!")
val getDecimalMatrixAsList = putDecimalMatrixAsList.asInstanceOf[Array[Array[Double]]]
val getDecimalMatrixAsListType = getDecimalMatrixAsList(0)(0).getClass()
println(getDecimalMatrixAsListType)
println(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != classOf[Double])
	throw new Exception("getDecimalMatrixAsList not Double!")

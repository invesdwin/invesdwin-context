println("getInteger")
if(getInteger != null)
	throw new Exception("getInteger already defined!")
val getInteger = putInteger
val getIntegerType = getInteger.getClass()
println(getIntegerType)
println(getInteger)
if(getIntegerType != classOf[java.lang.Integer])
	throw new Exception("getInteger not Integer!")

println("getIntegerVector")
if(getIntegerVector != null)
	throw new Exception("getIntegerVector already defined!")
val getIntegerVector = putIntegerVector.asInstanceOf[Array[Int]]
val getIntegerVectorType = getIntegerVector(0).getClass()
println(getIntegerVectorType)
println(getIntegerVector)
if(getIntegerVectorType != classOf[Int])
	throw new Exception("getIntegerVector not Integer!")

println("getIntegerVectorAsList")
if(getIntegerVectorAsList != null)
	throw new Exception("getIntegerVectorAsList already defined!")
val getIntegerVectorAsList = putIntegerVectorAsList.asInstanceOf[Array[Int]]
val getIntegerVectorAsListType = getIntegerVectorAsList(0).getClass()
println(getIntegerVectorAsListType)
println(getIntegerVectorAsList)
if(getIntegerVectorAsListType != classOf[Int])
	throw new Exception("getIntegerVectorAsList not Integer!")

println("getIntegerMatrix")
if(getIntegerMatrix != null)
	throw new Exception("getIntegerMatrix already defined!")
val getIntegerMatrix = putIntegerMatrix.asInstanceOf[Array[Array[Int]]]
val getIntegerMatrixType = getIntegerMatrix(0)(0).getClass()
println(getIntegerMatrixType)
println(getIntegerMatrix)
if(getIntegerMatrixType != classOf[Int])
	throw new Exception("getIntegerMatrix not Integer!")

println("getIntegerMatrixAsList")
if(getIntegerMatrixAsList != null)
	throw new Exception("getIntegerMatrixAsList already defined!")
val getIntegerMatrixAsList = putIntegerMatrixAsList.asInstanceOf[Array[Array[Int]]]
val getIntegerMatrixAsListType = getIntegerMatrixAsList(0)(0).getClass()
println(getIntegerMatrixAsListType)
println(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType != classOf[Int])
	throw new Exception("getIntegerMatrixAsList not Integer!")

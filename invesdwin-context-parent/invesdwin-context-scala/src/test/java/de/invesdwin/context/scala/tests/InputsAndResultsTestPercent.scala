println("getPercent")
if(getPercent != null)
	throw new Exception("getPercent already defined!")
val getPercent = putPercent
val getPercentType = getPercent.getClass()
println(getPercentType)
println(getPercent)
if(getPercentType != classOf[java.lang.Double])
	throw new Exception("getPercent not Double!")

println("getPercentVector")
if(getPercentVector != null)
	throw new Exception("getPercentVector already defined!")
val getPercentVector = putPercentVector.asInstanceOf[Array[Double]]
val getPercentVectorType = getPercentVector(0).getClass()
println(getPercentVectorType)
println(getPercentVector)
if(getPercentVectorType != classOf[Double])
	throw new Exception("getPercentVector not Double!")

println("getPercentVectorAsList")
if(getPercentVectorAsList != null)
	throw new Exception("getPercentVectorAsList already defined!")
val getPercentVectorAsList = putPercentVectorAsList.asInstanceOf[Array[Double]]
val getPercentVectorAsListType = getPercentVectorAsList(0).getClass()
println(getPercentVectorAsListType)
println(getPercentVectorAsList)
if(getPercentVectorAsListType != classOf[Double])
	throw new Exception("getPercentVectorAsList not Double!")

println("getPercentMatrix")
if(getPercentMatrix != null)
	throw new Exception("getPercentMatrix already defined!")
val getPercentMatrix = putPercentMatrix.asInstanceOf[Array[Array[Double]]]
val getPercentMatrixType = getPercentMatrix(0)(0).getClass()
println(getPercentMatrixType)
println(getPercentMatrix)
if(getPercentMatrixType != classOf[Double])
	throw new Exception("getPercentMatrix not Double!")

println("getPercentMatrixAsList")
if(getPercentMatrixAsList != null)
	throw new Exception("getPercentMatrixAsList already defined!")
val getPercentMatrixAsList = putPercentMatrixAsList.asInstanceOf[Array[Array[Double]]]
val getPercentMatrixAsListType = getPercentMatrixAsList(0)(0).getClass()
println(getPercentMatrixAsListType)
println(getPercentMatrixAsList)
if(getPercentMatrixAsListType != classOf[Double])
	throw new Exception("getPercentMatrixAsList not Double!")

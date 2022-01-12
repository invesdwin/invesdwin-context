println("getLong")
if(getLong != null)
	throw new Exception("getLong already defined!")
val getLong = putLong
val getLongType = getLong.getClass()
println(getLongType)
println(getLong)
if(getLongType != classOf[java.lang.Long])
	throw new Exception("getLong not Long!")

println("getLongVector")
if(getLongVector != null)
	throw new Exception("getLongVector already defined!")
val getLongVector = putLongVector.asInstanceOf[Array[Long]]
val getLongVectorType = getLongVector(0).getClass()
println(getLongVectorType)
println(getLongVector)
if(getLongVectorType != classOf[Long])
	throw new Exception("getLongVector not Long!")

println("getLongVectorAsList")
if(getLongVectorAsList != null)
	throw new Exception("getLongVectorAsList already defined!")
val getLongVectorAsList = putLongVectorAsList.asInstanceOf[Array[Long]]
val getLongVectorAsListType = getLongVectorAsList(0).getClass()
println(getLongVectorAsListType)
println(getLongVectorAsList)
if(getLongVectorAsListType != classOf[Long])
	throw new Exception("getLongVectorAsList not Long!")

println("getLongMatrix")
if(getLongMatrix != null)
	throw new Exception("getLongMatrix already defined!")
val getLongMatrix = putLongMatrix.asInstanceOf[Array[Array[Long]]]
val getLongMatrixType = getLongMatrix(0)(0).getClass()
println(getLongMatrixType)
println(getLongMatrix)
if(getLongMatrixType != classOf[Long])
	throw new Exception("getLongMatrix not Long!")

println("getLongMatrixAsList")
if(getLongMatrixAsList != null)
	throw new Exception("getLongMatrixAsList already defined!")
val getLongMatrixAsList = putLongMatrixAsList.asInstanceOf[Array[Array[Long]]]
val getLongMatrixAsListType = getLongMatrixAsList(0)(0).getClass()
println(getLongMatrixAsListType)
println(getLongMatrixAsList)
if(getLongMatrixAsListType != classOf[Long])
	throw new Exception("getLongMatrixAsList not Long!")

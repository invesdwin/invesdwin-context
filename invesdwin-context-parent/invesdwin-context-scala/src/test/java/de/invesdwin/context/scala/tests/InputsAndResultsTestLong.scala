println("getLong")
if(binding.hasVariable("getLong"))
	throw new Exception("getLong already defined!")
val getLong = putLong
val getLongType = getLong.getClass()
println(getLongType)
println(getLong)
if(getLongType != Long.class)
	throw new Exception("getLong not Long!")

println("getLongVector")
if(binding.hasVariable("getLongVector"))
	throw new Exception("getLongVector already defined!")
val getLongVector = putLongVector
val getLongVectorType = getLongVector[0].getClass()
println(getLongVectorType)
println(getLongVector)
if(getLongVectorType != Long.class)
	throw new Exception("getLongVector not Long!")

println("getLongVectorAsList")
if(binding.hasVariable("getLongVectorAsList"))
	throw new Exception("getLongVectorAsList already defined!")
val getLongVectorAsList = putLongVectorAsList
val getLongVectorAsListType = getLongVectorAsList[0].getClass()
println(getLongVectorAsListType)
println(getLongVectorAsList)
if(getLongVectorAsListType != Long.class)
	throw new Exception("getLongVectorAsList not Long!")

println("getLongMatrix")
if(binding.hasVariable("getLongMatrix"))
	throw new Exception("getLongMatrix already defined!")
val getLongMatrix = putLongMatrix
val getLongMatrixType = getLongMatrix[0][0].getClass()
println(getLongMatrixType)
println(getLongMatrix)
if(getLongMatrixType != Long.class)
	throw new Exception("getLongMatrix not Long!")

println("getLongMatrixAsList")
if(binding.hasVariable("getLongMatrixAsList"))
	throw new Exception("getLongMatrixAsList already defined!")
val getLongMatrixAsList = putLongMatrixAsList
val getLongMatrixAsListType = getLongMatrixAsList[0][0].getClass()
println(getLongMatrixAsListType)
println(getLongMatrixAsList)
if(getLongMatrixAsListType != Long.class)
	throw new Exception("getLongMatrixAsList not Long!")

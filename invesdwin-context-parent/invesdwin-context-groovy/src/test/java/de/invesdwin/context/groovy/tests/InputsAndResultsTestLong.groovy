println("getLong")
if(binding.hasVariable('getLong'))
	throw new Exception("getLong already defined!")
getLong = putLong
getLongType = getLong.getClass()
println(getLongType)
println(getLong)
if(getLongType is not long):
	throw new Exception("getLong not Long!")

println("getLongVector")
if(binding.hasVariable('getLongVector'))
	throw new Exception("getLongVector already defined!")
getLongVector = putLongVector
getLongVectorType = getLongVector[0].getClass()
println(getLongVectorType)
println(getLongVector)
if(getLongVectorType is not long):
	throw new Exception("getLongVector not Long!")

println("getLongVectorAsList")
if(binding.hasVariable('getLongVectorAsList'))
	throw new Exception("getLongVectorAsList already defined!")
getLongVectorAsList = putLongVectorAsList
getLongVectorAsListType = getLongVectorAsList[0].getClass()
println(getLongVectorAsListType)
println(getLongVectorAsList)
if(getLongVectorAsListType is not long):
	throw new Exception("getLongVectorAsList not Long!")

println("getLongMatrix")
if(binding.hasVariable('getLongMatrix'))
	throw new Exception("getLongMatrix already defined!")
getLongMatrix = putLongMatrix
getLongMatrixType = getLongMatrix[0][0].getClass()
println(getLongMatrixType)
println(getLongMatrix)
if(getLongMatrixType is not long):
	throw new Exception("getLongMatrix not Long!")

println("getLongMatrixAsList")
if(binding.hasVariable('getLongMatrixAsList'))
	throw new Exception("getLongMatrixAsList already defined!")
getLongMatrixAsList = putLongMatrixAsList
getLongMatrixAsListType = getLongMatrixAsList[0][0].getClass()
println(getLongMatrixAsListType)
println(getLongMatrixAsList)
if(getLongMatrixAsListType is not long):
	throw new Exception("getLongMatrixAsList not Long!")

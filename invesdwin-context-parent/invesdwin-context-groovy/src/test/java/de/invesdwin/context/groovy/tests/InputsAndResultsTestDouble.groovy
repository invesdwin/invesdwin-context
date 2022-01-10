println("getDouble")
if(binding.hasVariable('getDouble'))
	throw new Exception("getDouble already defined!")
getDouble = putDouble
getDoubleType = getDouble.getClass()
println(getDoubleType)
println(getDouble)
if(getDoubleType is not float):
	throw new Exception("getDouble not Double!")

println("getDoubleVector")
if(binding.hasVariable('getDoubleVector'))
	throw new Exception("getDoubleVector already defined!")
getDoubleVector = putDoubleVector
getDoubleVectorType = getDoubleVector[0].getClass()
println(getDoubleVectorType)
println(getDoubleVector)
if(getDoubleVectorType is not float):
	throw new Exception("getDoubleVector not Double!")

println("getDoubleVectorAsList")
if(binding.hasVariable('getDoubleVectorAsList'))
	throw new Exception("getDoubleVectorAsList already defined!")
getDoubleVectorAsList = putDoubleVectorAsList
getDoubleVectorAsListType = getDoubleVectorAsList[0].getClass()
println(getDoubleVectorAsListType)
println(getDoubleVectorAsList)
if(getDoubleVectorAsListType is not float):
	throw new Exception("getDoubleVectorAsList not Double!")

println("getDoubleMatrix")
if(binding.hasVariable('getDoubleMatrix'))
	throw new Exception("getDoubleMatrix already defined!")
getDoubleMatrix = putDoubleMatrix
getDoubleMatrixType = getDoubleMatrix[0][0].getClass()
println(getDoubleMatrixType)
println(getDoubleMatrix)
if(getDoubleMatrixType is not float):
	throw new Exception("getDoubleMatrix not Double!")

println("getDoubleMatrixAsList")
if(binding.hasVariable('getDoubleMatrixAsList'))
	throw new Exception("getDoubleMatrixAsList already defined!")
getDoubleMatrixAsList = putDoubleMatrixAsList
getDoubleMatrixAsListType = getDoubleMatrixAsList[0][0].getClass()
println(getDoubleMatrixAsListType)
println(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType is not float):
	throw new Exception("getDoubleMatrixAsList not Double!")

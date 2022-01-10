println("getInteger")
if(binding.hasVariable('getInteger'))
	throw new Exception("getInteger already defined!")
getInteger = putInteger
getIntegerType = getInteger.getClass()
println(getIntegerType)
println(getInteger)
if(getIntegerType is not int):
	throw new Exception("getInteger not Integer!")

println("getIntegerVector")
if(binding.hasVariable('getIntegerVector'))
	throw new Exception("getIntegerVector already defined!")
getIntegerVector = putIntegerVector
getIntegerVectorType = getIntegerVector[0].getClass()
println(getIntegerVectorType)
println(getIntegerVector)
if(getIntegerVectorType is not int):
	throw new Exception("getIntegerVector not Integer!")

println("getIntegerVectorAsList")
if(binding.hasVariable('getIntegerVectorAsList'))
	throw new Exception("getIntegerVectorAsList already defined!")
getIntegerVectorAsList = putIntegerVectorAsList
getIntegerVectorAsListType = getIntegerVectorAsList[0].getClass()
println(getIntegerVectorAsListType)
println(getIntegerVectorAsList)
if(getIntegerVectorAsListType is not int):
	throw new Exception("getIntegerVectorAsList not Integer!")

println("getIntegerMatrix")
if(binding.hasVariable('getIntegerMatrix'))
	throw new Exception("getIntegerMatrix already defined!")
getIntegerMatrix = putIntegerMatrix
getIntegerMatrixType = getIntegerMatrix[0][0].getClass()
println(getIntegerMatrixType)
println(getIntegerMatrix)
if(getIntegerMatrixType is not int):
	throw new Exception("getIntegerMatrix not Integer!")

println("getIntegerMatrixAsList")
if(binding.hasVariable('getIntegerMatrixAsList'))
	throw new Exception("getIntegerMatrixAsList already defined!")
getIntegerMatrixAsList = putIntegerMatrixAsList
getIntegerMatrixAsListType = getIntegerMatrixAsList[0][0].getClass()
println(getIntegerMatrixAsListType)
println(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType is not int):
	throw new Exception("getIntegerMatrixAsList not Integer!")

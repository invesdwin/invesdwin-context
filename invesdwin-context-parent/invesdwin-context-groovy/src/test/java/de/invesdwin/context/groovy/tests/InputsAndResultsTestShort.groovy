println("getShort")
if(binding.hasVariable('getShort'))
	throw new Exception("getShort already defined!")
getShort = putShort
getShortType = getShort.getClass()
println(getShortType)
println(getShort)
if(getShortType is not int):
	throw new Exception("getShort not Short!")

println("getShortVector")
if(binding.hasVariable('getShortVector'))
	throw new Exception("getShortVector already defined!")
getShortVector = putShortVector
getShortVectorType = getShortVector[0].getClass()
println(getShortVectorType)
println(getShortVector)
if(getShortVectorType is not int):
	throw new Exception("getShortVector not Short!")

println("getShortVectorAsList")
if(binding.hasVariable('getShortVectorAsList'))
	throw new Exception("getShortVectorAsList already defined!")
getShortVectorAsList = putShortVectorAsList
getShortVectorAsListType = getShortVectorAsList[0].getClass()
println(getShortVectorAsListType)
println(getShortVectorAsList)
if(getShortVectorAsListType is not int):
	throw new Exception("getShortVectorAsList not Short!")

println("getShortMatrix")
if(binding.hasVariable('getShortMatrix'))
	throw new Exception("getShortMatrix already defined!")
getShortMatrix = putShortMatrix
getShortMatrixType = getShortMatrix[0][0].getClass()
println(getShortMatrixType)
println(getShortMatrix)
if(getShortMatrixType is not int):
	throw new Exception("getShortMatrix not Short!")

println("getShortMatrixAsList")
if(binding.hasVariable('getShortMatrixAsList'))
	throw new Exception("getShortMatrixAsList already defined!")
getShortMatrixAsList = putShortMatrixAsList
getShortMatrixAsListType = getShortMatrixAsList[0][0].getClass()
println(getShortMatrixAsListType)
println(getShortMatrixAsList)
if(getShortMatrixAsListType is not int):
	throw new Exception("getShortMatrixAsList not Short!")

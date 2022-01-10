println("getBoolean")
if(binding.hasVariable('getBoolean'))
	throw new Exception("getBoolean already defined!")
getBoolean = putBoolean
println(getBoolean.getClass())
println(getBoolean)
getBooleanType = getBoolean.getClass()
if(getBooleanType != Boolean.class)
	throw new Exception("getBoolean not Boolean!")

println("getBooleanVector")
if(binding.hasVariable('getBooleanVector'))
	throw new Exception("getBooleanVector already defined!")
getBooleanVector = putBooleanVector
getBooleanVectorType = getBooleanVector[0].getClass()
println(getBooleanVectorType)
println(getBooleanVector)
getBooleanVectorType = getBooleanVectorType
if(getBooleanVectorType != Boolean.class)
	throw new Exception("getBooleanVector not Boolean!")

println("getBooleanVectorAsList")
if(binding.hasVariable('getBooleanVectorAsList'))
	throw new Exception("getBooleanVectorAsList already defined!")
getBooleanVectorAsList = putBooleanVectorAsList
getBooleanVectorAsListType = getBooleanVectorAsList[0].getClass()
println(getBooleanVectorAsListType)
println(getBooleanVectorAsList)
getBooleanVectorAsListType = getBooleanVectorAsListType
if(getBooleanVectorAsListType != Boolean.class)
	throw new Exception("getBooleanVectorAsList not Boolean!")

println("getBooleanMatrix")
if(binding.hasVariable('getBooleanMatrix'))
	throw new Exception("getBooleanMatrix already defined!")
getBooleanMatrix = putBooleanMatrix
getBooleanMatrixType = getBooleanMatrix[0][0].getClass()
println(getBooleanMatrixType)
println(getBooleanMatrix)
getBooleanMatrixType = getBooleanMatrixType
if(getBooleanMatrixType != Boolean.class)
	throw new Exception("getBooleanMatrix not Boolean!")

println("getBooleanMatrixAsList")
if(binding.hasVariable('getBooleanMatrixAsList'))
	throw new Exception("getBooleanMatrixAsList already defined!")
getBooleanMatrixAsList = putBooleanMatrixAsList
getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].getClass()
println(getBooleanMatrixAsListType)
println(getBooleanMatrixAsList)
getBooleanMatrixAsListType = getBooleanMatrixAsListType
if(getBooleanMatrixAsListType != Boolean.class)
	throw new Exception("getBooleanMatrixAsList not Boolean!")

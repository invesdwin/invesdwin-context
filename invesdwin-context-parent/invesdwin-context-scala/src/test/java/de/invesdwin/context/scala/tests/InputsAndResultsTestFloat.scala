println("getFloat")
if(binding.hasVariable('getFloat'))
	throw new Exception("getFloat already defined!")
getFloat = putFloat
getFloatType = getFloat.getClass()
println(getFloatType)
println(getFloat)
if(getFloatType != Float.class)
	throw new Exception("getFloat not Float!")

println("getFloatVector")
if(binding.hasVariable('getFloatVector'))
	throw new Exception("getFloatVector already defined!")
getFloatVector = putFloatVector
getFloatVectorType = getFloatVector[0].getClass()
println(getFloatVectorType)
println(getFloatVector)
if(getFloatVectorType != Float.class)
	throw new Exception("getFloatVector not Float!")

println("getFloatVectorAsList")
if(binding.hasVariable('getFloatVectorAsList'))
	throw new Exception("getFloatVectorAsList already defined!")
getFloatVectorAsList = putFloatVectorAsList
getFloatVectorAsListType = getFloatVectorAsList[0].getClass()
println(getFloatVectorAsListType)
println(getFloatVectorAsList)
if(getFloatVectorAsListType != Float.class)
	throw new Exception("getFloatVectorAsList not Float!")

println("getFloatMatrix")
if(binding.hasVariable('getFloatMatrix'))
	throw new Exception("getFloatMatrix already defined!")
getFloatMatrix = putFloatMatrix
getFloatMatrixType = getFloatMatrix[0][0].getClass()
println(getFloatMatrixType)
println(getFloatMatrix)
if(getFloatMatrixType != Float.class)
	throw new Exception("getFloatMatrix not Float!")

println("getFloatMatrixAsList")
if(binding.hasVariable('getFloatMatrixAsList'))
	throw new Exception("getFloatMatrixAsList already defined!")
getFloatMatrixAsList = putFloatMatrixAsList
getFloatMatrixAsListType = getFloatMatrixAsList[0][0].getClass()
println(getFloatMatrixAsListType)
println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float.class)
	throw new Exception("getFloatMatrixAsList not Float!")

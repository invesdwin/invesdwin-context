println("getFloat")
if(binding.hasVariable("getFloat"))
	throw new Exception("getFloat already defined!")
val getFloat = putFloat
val getFloatType = getFloat.getClass()
println(getFloatType)
println(getFloat)
if(getFloatType != Float.class)
	throw new Exception("getFloat not Float!")

println("getFloatVector")
if(binding.hasVariable("getFloatVector"))
	throw new Exception("getFloatVector already defined!")
val getFloatVector = putFloatVector
val getFloatVectorType = getFloatVector[0].getClass()
println(getFloatVectorType)
println(getFloatVector)
if(getFloatVectorType != Float.class)
	throw new Exception("getFloatVector not Float!")

println("getFloatVectorAsList")
if(binding.hasVariable("getFloatVectorAsList"))
	throw new Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList = putFloatVectorAsList
val getFloatVectorAsListType = getFloatVectorAsList[0].getClass()
println(getFloatVectorAsListType)
println(getFloatVectorAsList)
if(getFloatVectorAsListType != Float.class)
	throw new Exception("getFloatVectorAsList not Float!")

println("getFloatMatrix")
if(binding.hasVariable("getFloatMatrix"))
	throw new Exception("getFloatMatrix already defined!")
val getFloatMatrix = putFloatMatrix
val getFloatMatrixType = getFloatMatrix[0][0].getClass()
println(getFloatMatrixType)
println(getFloatMatrix)
if(getFloatMatrixType != Float.class)
	throw new Exception("getFloatMatrix not Float!")

println("getFloatMatrixAsList")
if(binding.hasVariable("getFloatMatrixAsList"))
	throw new Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList = putFloatMatrixAsList
val getFloatMatrixAsListType = getFloatMatrixAsList[0][0].getClass()
println(getFloatMatrixAsListType)
println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float.class)
	throw new Exception("getFloatMatrixAsList not Float!")

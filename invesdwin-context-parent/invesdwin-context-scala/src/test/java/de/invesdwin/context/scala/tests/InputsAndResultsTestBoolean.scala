println("getBoolean")
if(binding.hasVariable("getBoolean"))
	throw new Exception("getBoolean already defined!")
val getBoolean = putBoolean
val getBooleanType = getBoolean.getClass()
println(getBooleanType)
println(getBoolean)
if(getBooleanType != Boolean.class)
	throw new Exception("getBoolean not Boolean!")

println("getBooleanVector")
if(binding.hasVariable("getBooleanVector"))
	throw new Exception("getBooleanVector already defined!")
val getBooleanVector = putBooleanVector
val getBooleanVectorType = getBooleanVector[0].getClass()
println(getBooleanVectorType)
println(getBooleanVector)
if(getBooleanVectorType != Boolean.class)
	throw new Exception("getBooleanVector not Boolean!")

println("getBooleanVectorAsList")
if(binding.hasVariable("getBooleanVectorAsList"))
	throw new Exception("getBooleanVectorAsList already defined!")
val getBooleanVectorAsList = putBooleanVectorAsList
val getBooleanVectorAsListType = getBooleanVectorAsList[0].getClass()
println(getBooleanVectorAsListType)
println(getBooleanVectorAsList)
if(getBooleanVectorAsListType != Boolean.class)
	throw new Exception("getBooleanVectorAsList not Boolean!")

println("getBooleanMatrix")
if(binding.hasVariable("getBooleanMatrix"))
	throw new Exception("getBooleanMatrix already defined!")
val getBooleanMatrix = putBooleanMatrix
val getBooleanMatrixType = getBooleanMatrix[0][0].getClass()
println(getBooleanMatrixType)
println(getBooleanMatrix)
if(getBooleanMatrixType != Boolean.class)
	throw new Exception("getBooleanMatrix not Boolean!")

println("getBooleanMatrixAsList")
if(binding.hasVariable("getBooleanMatrixAsList"))
	throw new Exception("getBooleanMatrixAsList already defined!")
val getBooleanMatrixAsList = putBooleanMatrixAsList
val getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].getClass()
println(getBooleanMatrixAsListType)
println(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != Boolean.class)
	throw new Exception("getBooleanMatrixAsList not Boolean!")

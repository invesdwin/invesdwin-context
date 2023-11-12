println("getFloat")
if(binding.containsKey("getFloat"))
	throw Exception("getFloat already defined!")
val getFloat = putFloat
val getFloatType = getFloat::class
println(getFloatType)
println(getFloat)
if(getFloatType != Float::class)
	throw Exception("getFloat not Float!")

println("getFloatVector")
if(binding.containsKey("getFloatVector"))
	throw Exception("getFloatVector already defined!")
val getFloatVector = putFloatVector
val getFloatVectorType = getFloatVector[0]::class
println(getFloatVectorType)
println(getFloatVector)
if(getFloatVectorType != Float::class)
	throw Exception("getFloatVector not Float!")

println("getFloatVectorAsList")
if(binding.containsKey("getFloatVectorAsList"))
	throw Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList = putFloatVectorAsList
val getFloatVectorAsListType = getFloatVectorAsList[0]::class
println(getFloatVectorAsListType)
println(getFloatVectorAsList)
if(getFloatVectorAsListType != Float::class)
	throw Exception("getFloatVectorAsList not Float!")

println("getFloatMatrix")
if(binding.containsKey("getFloatMatrix"))
	throw Exception("getFloatMatrix already defined!")
val getFloatMatrix = putFloatMatrix as Array<FloatArray>
val getFloatMatrixType = getFloatMatrix[0][0]::class
println(getFloatMatrixType)
println(getFloatMatrix)
if(getFloatMatrixType != Float::class)
	throw Exception("getFloatMatrix not Float!")

println("getFloatMatrixAsList")
if(binding.containsKey("getFloatMatrixAsList"))
	throw Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList = putFloatMatrixAsList as Array<FloatArray>
val getFloatMatrixAsListType = getFloatMatrixAsList[0][0]::class
println(getFloatMatrixAsListType)
println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float::class)
	throw Exception("getFloatMatrixAsList not Float!")

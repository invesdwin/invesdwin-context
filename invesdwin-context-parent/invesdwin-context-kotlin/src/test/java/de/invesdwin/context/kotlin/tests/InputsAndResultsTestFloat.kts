println("getFloat")
if(bindings.containsKey("getFloat"))
	throw Exception("getFloat already defined!")
val getFloat = putFloat
val getFloatType = getFloat::class
println(getFloatType)
println(getFloat)
if(getFloatType != Float::class)
	throw Exception("getFloat not Float!")

println("getFloatVector")
if(bindings.containsKey("getFloatVector"))
	throw Exception("getFloatVector already defined!")
val getFloatVector = putFloatVector
val getFloatVectorType = getFloatVector[0]::class
println(getFloatVectorType)
println(getFloatVector)
if(getFloatVectorType != Float::class)
	throw Exception("getFloatVector not Float!")

println("getFloatVectorAsList")
if(bindings.containsKey("getFloatVectorAsList"))
	throw Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList = putFloatVectorAsList
val getFloatVectorAsListType = getFloatVectorAsList[0]::class
println(getFloatVectorAsListType)
println(getFloatVectorAsList)
if(getFloatVectorAsListType != Float::class)
	throw Exception("getFloatVectorAsList not Float!")

println("getFloatMatrix")
if(bindings.containsKey("getFloatMatrix"))
	throw Exception("getFloatMatrix already defined!")
val getFloatMatrix = putFloatMatrix
val getFloatMatrixRow = getFloatMatrix[0] as FloatArray
val getFloatMatrixType = getFloatMatrixRow[0]::class
println(getFloatMatrixType)
println(getFloatMatrix)
if(getFloatMatrixType != Float::class)
	throw Exception("getFloatMatrix not Float!")

println("getFloatMatrixAsList")
if(bindings.containsKey("getFloatMatrixAsList"))
	throw Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList = putFloatMatrixAsList
val getFloatMatrixAsListRow = getFloatMatrixAsList[0] as FloatArray
val getFloatMatrixAsListType = getFloatMatrixAsListRow[0]::class
println(getFloatMatrixAsListType)
println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float::class)
	throw Exception("getFloatMatrixAsList not Float!")

println("getFloat")
if(bindings.containsKey("getFloat"))
	throw Exception("getFloat already defined!")
val getFloat: Float = callback("getFloat")
val getFloatType = getFloat::class
println(getFloatType)
println(getFloat)
if(getFloatType != Float::class)
	throw Exception("getFloat not Float!")
callback<Unit>("setFloat",getFloat)

println("getFloatVector")
if(bindings.containsKey("getFloatVector"))
	throw Exception("getFloatVector already defined!")
val getFloatVector: FloatArray = callback("getFloatVector")
val getFloatVectorType = getFloatVector[0]::class
println(getFloatVectorType)
println(getFloatVector)
if(getFloatVectorType != Float::class)
	throw Exception("getFloatVector not Float!")
callback<Unit>("setFloatVector",getFloatVector)

println("getFloatVectorAsList")
if(bindings.containsKey("getFloatVectorAsList"))
	throw Exception("getFloatVectorAsList already defined!")
val getFloatVectorAsList: FloatArray = callback("getFloatVectorAsList")
val getFloatVectorAsListType = getFloatVectorAsList[0]::class
println(getFloatVectorAsListType)
println(getFloatVectorAsList)
if(getFloatVectorAsListType != Float::class)
	throw Exception("getFloatVectorAsList not Float!")
callback<Unit>("setFloatVectorAsList",getFloatVectorAsList)

println("getFloatMatrix")
if(bindings.containsKey("getFloatMatrix"))
	throw Exception("getFloatMatrix already defined!")
val getFloatMatrix: Array<FloatArray> = callback("getFloatMatrix")
val getFloatMatrixType = getFloatMatrix[0][0]::class
println(getFloatMatrixType)
println(getFloatMatrix)
if(getFloatMatrixType != Float::class)
	throw Exception("getFloatMatrix not Float!")
callback<Unit>("setFloatMatrix",getFloatMatrix)

println("getFloatMatrixAsList")
if(bindings.containsKey("getFloatMatrixAsList"))
	throw Exception("getFloatMatrixAsList already defined!")
val getFloatMatrixAsList: Array<FloatArray> = callback("getFloatMatrixAsList")
val getFloatMatrixAsListType = getFloatMatrixAsList[0][0]::class
println(getFloatMatrixAsListType)
println(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float::class)
	throw Exception("getFloatMatrixAsList not Float!")
callback<Unit>("setFloatMatrixAsList",getFloatMatrixAsList)
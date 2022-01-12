println("getBoolean")
if(bindings.containsKey("getBoolean"))
	throw Exception("getBoolean already defined!")
val getBoolean = putBoolean
val getBooleanType = getBoolean::class
println(getBooleanType)
println(getBoolean)
if(getBooleanType != Boolean::class)
	throw Exception("getBoolean not Boolean!")

println("getBooleanVector")
if(bindings.containsKey("getBooleanVector"))
	throw Exception("getBooleanVector already defined!")
val getBooleanVector = putBooleanVector
val getBooleanVectorType = getBooleanVector[0]::class
println(getBooleanVectorType)
println(getBooleanVector)
if(getBooleanVectorType != Boolean::class)
	throw Exception("getBooleanVector not Boolean!")

println("getBooleanVectorAsList")
if(bindings.containsKey("getBooleanVectorAsList"))
	throw Exception("getBooleanVectorAsList already defined!")
val getBooleanVectorAsList = putBooleanVectorAsList
val getBooleanVectorAsListType = getBooleanVectorAsList[0]::class
println(getBooleanVectorAsListType)
println(getBooleanVectorAsList)
if(getBooleanVectorAsListType != Boolean::class)
	throw Exception("getBooleanVectorAsList not Boolean!")

println("getBooleanMatrix")
if(bindings.containsKey("getBooleanMatrix"))
	throw Exception("getBooleanMatrix already defined!")
val getBooleanMatrix = putBooleanMatrix as Array<BooleanArray>
val getBooleanMatrixType = getBooleanMatrix[0][0]::class
println(getBooleanMatrixType)
println(getBooleanMatrix)
if(getBooleanMatrixType != Boolean::class)
	throw Exception("getBooleanMatrix not Boolean!")

println("getBooleanMatrixAsList")
if(bindings.containsKey("getBooleanMatrixAsList"))
	throw Exception("getBooleanMatrixAsList already defined!")
val getBooleanMatrixAsList = putBooleanMatrixAsList as Array<BooleanArray>
val getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0]::class
println(getBooleanMatrixAsListType)
println(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != Boolean::class)
	throw Exception("getBooleanMatrixAsList not Boolean!")

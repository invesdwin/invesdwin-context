println("getDouble")
if(bindings.containsKey("getDouble"))
	throw Exception("getDouble already defined!")
val getDouble = putDouble
val getDoubleType = getDouble::class
println(getDoubleType)
println(getDouble)
if(getDoubleType != Double::class)
	throw Exception("getDouble not Double!")

println("getDoubleVector")
if(bindings.containsKey("getDoubleVector"))
	throw Exception("getDoubleVector already defined!")
val getDoubleVector = putDoubleVector
val getDoubleVectorType = getDoubleVector[0]::class
println(getDoubleVectorType)
println(getDoubleVector)
if(getDoubleVectorType != Double::class)
	throw Exception("getDoubleVector not Double!")

println("getDoubleVectorAsList")
if(bindings.containsKey("getDoubleVectorAsList"))
	throw Exception("getDoubleVectorAsList already defined!")
val getDoubleVectorAsList = putDoubleVectorAsList
val getDoubleVectorAsListType = getDoubleVectorAsList[0]::class
println(getDoubleVectorAsListType)
println(getDoubleVectorAsList)
if(getDoubleVectorAsListType != Double::class)
	throw Exception("getDoubleVectorAsList not Double!")

println("getDoubleMatrix")
if(bindings.containsKey("getDoubleMatrix"))
	throw Exception("getDoubleMatrix already defined!")
val getDoubleMatrix = putDoubleMatrix
val getDoubleMatrixRow = getDoubleMatrix[0] as DoubleArray
val getDoubleMatrixType = getDoubleMatrixRow[0]::class
println(getDoubleMatrixType)
println(getDoubleMatrix)
if(getDoubleMatrixType != Double::class)
	throw Exception("getDoubleMatrix not Double!")

println("getDoubleMatrixAsList")
if(bindings.containsKey("getDoubleMatrixAsList"))
	throw Exception("getDoubleMatrixAsList already defined!")
val getDoubleMatrixAsList = putDoubleMatrixAsList
val getDoubleMatrixAsListRow = getDoubleMatrixAsList[0] as DoubleArray
val getDoubleMatrixAsListType = getDoubleMatrixAsListRow[0]::class
println(getDoubleMatrixAsListType)
println(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != Double::class)
	throw Exception("getDoubleMatrixAsList not Double!")

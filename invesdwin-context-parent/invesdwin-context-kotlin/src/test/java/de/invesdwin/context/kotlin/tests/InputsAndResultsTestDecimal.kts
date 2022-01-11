println("getDecimal")
if(bindings.containsKey("getDecimal"))
	throw Exception("getDecimal already defined!")
val getDecimal = putDecimal
val getDecimalType = getDecimal::class
println(getDecimalType)
println(getDecimal)
if(getDecimalType != Double::class)
	throw Exception("getDecimal not Double!")

println("getDecimalVector")
if(bindings.containsKey("getDecimalVector"))
	throw Exception("getDecimalVector already defined!")
val getDecimalVector = putDecimalVector
val getDecimalVectorType = getDecimalVector[0]::class
println(getDecimalVectorType)
println(getDecimalVector)
if(getDecimalVectorType != Double::class)
	throw Exception("getDecimalVector not Double!")

println("getDecimalVectorAsList")
if(bindings.containsKey("getDecimalVectorAsList"))
	throw Exception("getDecimalVectorAsList already defined!")
val getDecimalVectorAsList = putDecimalVectorAsList
val getDecimalVectorAsListType = getDecimalVectorAsList[0]::class
println(getDecimalVectorAsListType)
println(getDecimalVectorAsList)
if(getDecimalVectorAsListType != Double::class)
	throw Exception("getDecimalVectorAsList not Double!")

println("getDecimalMatrix")
if(bindings.containsKey("getDecimalMatrix"))
	throw Exception("getDecimalMatrix already defined!")
val getDecimalMatrix = putDecimalMatrix
val getDecimalMatrixRow = getDecimalMatrix[0] as DoubleArray
val getDecimalMatrixType = getDecimalMatrixRow[0]::class
println(getDecimalMatrixType)
println(getDecimalMatrix)
if(getDecimalMatrixType != Double::class)
	throw Exception("getDecimalMatrix not Double!")

println("getDecimalMatrixAsList")
if(bindings.containsKey("getDecimalMatrixAsList"))
	throw Exception("getDecimalMatrixAsList already defined!")
val getDecimalMatrixAsList = putDecimalMatrixAsList
val getDecimalMatrixAsListRow = getDecimalMatrixAsList[0] as DoubleArray
val getDecimalMatrixAsListType = getDecimalMatrixAsListRow[0]::class
println(getDecimalMatrixAsListType)
println(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != Double::class)
	throw Exception("getDecimalMatrixAsList not Double!")

println("getDecimal")
if(binding.containsKey("getDecimal"))
	throw Exception("getDecimal already defined!")
val getDecimal = putDecimal
val getDecimalType = getDecimal::class
println(getDecimalType)
println(getDecimal)
if(getDecimalType != Double::class)
	throw Exception("getDecimal not Double!")

println("getDecimalVector")
if(binding.containsKey("getDecimalVector"))
	throw Exception("getDecimalVector already defined!")
val getDecimalVector = putDecimalVector
val getDecimalVectorType = getDecimalVector[0]::class
println(getDecimalVectorType)
println(getDecimalVector)
if(getDecimalVectorType != Double::class)
	throw Exception("getDecimalVector not Double!")

println("getDecimalVectorAsList")
if(binding.containsKey("getDecimalVectorAsList"))
	throw Exception("getDecimalVectorAsList already defined!")
val getDecimalVectorAsList = putDecimalVectorAsList
val getDecimalVectorAsListType = getDecimalVectorAsList[0]::class
println(getDecimalVectorAsListType)
println(getDecimalVectorAsList)
if(getDecimalVectorAsListType != Double::class)
	throw Exception("getDecimalVectorAsList not Double!")

println("getDecimalMatrix")
if(binding.containsKey("getDecimalMatrix"))
	throw Exception("getDecimalMatrix already defined!")
val getDecimalMatrix = putDecimalMatrix as Array<DoubleArray>
val getDecimalMatrixType = getDecimalMatrix[0][0]::class
println(getDecimalMatrixType)
println(getDecimalMatrix)
if(getDecimalMatrixType != Double::class)
	throw Exception("getDecimalMatrix not Double!")

println("getDecimalMatrixAsList")
if(binding.containsKey("getDecimalMatrixAsList"))
	throw Exception("getDecimalMatrixAsList already defined!")
val getDecimalMatrixAsList = putDecimalMatrixAsList as Array<DoubleArray>
val getDecimalMatrixAsListType = getDecimalMatrixAsList[0][0]::class
println(getDecimalMatrixAsListType)
println(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != Double::class)
	throw Exception("getDecimalMatrixAsList not Double!")

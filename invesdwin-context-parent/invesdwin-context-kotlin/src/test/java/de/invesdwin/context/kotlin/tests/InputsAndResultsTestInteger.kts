println("getInteger")
if(binding.containsKey("getInteger"))
	throw Exception("getInteger already defined!")
val getInteger = putInteger
val getIntegerType = getInteger::class
println(getIntegerType)
println(getInteger)
if(getIntegerType != Integer::class)
	throw Exception("getInteger not Integer!")

println("getIntegerVector")
if(binding.containsKey("getIntegerVector"))
	throw Exception("getIntegerVector already defined!")
val getIntegerVector = putIntegerVector
val getIntegerVectorType = getIntegerVector[0]::class
println(getIntegerVectorType)
println(getIntegerVector)
if(getIntegerVectorType != Integer::class)
	throw Exception("getIntegerVector not Integer!")

println("getIntegerVectorAsList")
if(binding.containsKey("getIntegerVectorAsList"))
	throw Exception("getIntegerVectorAsList already defined!")
val getIntegerVectorAsList = putIntegerVectorAsList
val getIntegerVectorAsListType = getIntegerVectorAsList[0]::class
println(getIntegerVectorAsListType)
println(getIntegerVectorAsList)
if(getIntegerVectorAsListType != Integer::class)
	throw Exception("getIntegerVectorAsList not Integer!")

println("getIntegerMatrix")
if(binding.containsKey("getIntegerMatrix"))
	throw Exception("getIntegerMatrix already defined!")
val getIntegerMatrix = putIntegerMatrix as Array<IntArray>
val getIntegerMatrixType = getIntegerMatrix[0][0]::class
println(getIntegerMatrixType)
println(getIntegerMatrix)
if(getIntegerMatrixType != Integer::class)
	throw Exception("getIntegerMatrix not Integer!")

println("getIntegerMatrixAsList")
if(binding.containsKey("getIntegerMatrixAsList"))
	throw Exception("getIntegerMatrixAsList already defined!")
val getIntegerMatrixAsList = putIntegerMatrixAsList as Array<IntArray>
val getIntegerMatrixAsListType = getIntegerMatrixAsList[0][0]::class
println(getIntegerMatrixAsListType)
println(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType != Integer::class)
	throw Exception("getIntegerMatrixAsList not Integer!")

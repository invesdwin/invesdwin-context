println("getPercent")
if(bindings.containsKey("getPercent"))
	throw Exception("getPercent already defined!")
val getPercent = putPercent
val getPercentType = getPercent::class
println(getPercentType)
println(getPercent)
if(getPercentType != Double::class)
	throw Exception("getPercent not Double!")

println("getPercentVector")
if(bindings.containsKey("getPercentVector"))
	throw Exception("getPercentVector already defined!")
val getPercentVector = putPercentVector
val getPercentVectorType = getPercentVector[0]::class
println(getPercentVectorType)
println(getPercentVector)
if(getPercentVectorType != Double::class)
	throw Exception("getPercentVector not Double!")

println("getPercentVectorAsList")
if(bindings.containsKey("getPercentVectorAsList"))
	throw Exception("getPercentVectorAsList already defined!")
val getPercentVectorAsList = putPercentVectorAsList
val getPercentVectorAsListType = getPercentVectorAsList[0]::class
println(getPercentVectorAsListType)
println(getPercentVectorAsList)
if(getPercentVectorAsListType != Double::class)
	throw Exception("getPercentVectorAsList not Double!")

println("getPercentMatrix")
if(bindings.containsKey("getPercentMatrix"))
	throw Exception("getPercentMatrix already defined!")
val getPercentMatrix = putPercentMatrix as Array<DoubleArray>
val getPercentMatrixType = getPercentMatrix[0][0]::class
println(getPercentMatrixType)
println(getPercentMatrix)
if(getPercentMatrixType != Double::class)
	throw Exception("getPercentMatrix not Double!")

println("getPercentMatrixAsList")
if(bindings.containsKey("getPercentMatrixAsList"))
	throw Exception("getPercentMatrixAsList already defined!")
val getPercentMatrixAsList = putPercentMatrixAsList as Array<DoubleArray>
val getPercentMatrixAsListType = getPercentMatrixAsList[0][0]::class
println(getPercentMatrixAsListType)
println(getPercentMatrixAsList)
if(getPercentMatrixAsListType != Double::class)
	throw Exception("getPercentMatrixAsList not Double!")

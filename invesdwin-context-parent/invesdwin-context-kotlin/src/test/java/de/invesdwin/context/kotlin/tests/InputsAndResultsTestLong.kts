println("getLong")
if(bindings.containsKey("getLong"))
	throw Exception("getLong already defined!")
val getLong = putLong
val getLongType = getLong::class
println(getLongType)
println(getLong)
if(getLongType != Long::class)
	throw Exception("getLong not Long!")

println("getLongVector")
if(bindings.containsKey("getLongVector"))
	throw Exception("getLongVector already defined!")
val getLongVector = putLongVector
val getLongVectorType = getLongVector[0]::class
println(getLongVectorType)
println(getLongVector)
if(getLongVectorType != Long::class)
	throw Exception("getLongVector not Long!")

println("getLongVectorAsList")
if(bindings.containsKey("getLongVectorAsList"))
	throw Exception("getLongVectorAsList already defined!")
val getLongVectorAsList = putLongVectorAsList
val getLongVectorAsListType = getLongVectorAsList[0]::class
println(getLongVectorAsListType)
println(getLongVectorAsList)
if(getLongVectorAsListType != Long::class)
	throw Exception("getLongVectorAsList not Long!")

println("getLongMatrix")
if(bindings.containsKey("getLongMatrix"))
	throw Exception("getLongMatrix already defined!")
val getLongMatrix = putLongMatrix
val getLongMatrixRow = getLongMatrix[0] as LongArray
val getLongMatrixType = getLongMatrixRow[0]::class
println(getLongMatrixType)
println(getLongMatrix)
if(getLongMatrixType != Long::class)
	throw Exception("getLongMatrix not Long!")

println("getLongMatrixAsList")
if(bindings.containsKey("getLongMatrixAsList"))
	throw Exception("getLongMatrixAsList already defined!")
val getLongMatrixAsList = putLongMatrixAsList
val getLongMatrixAsListRow = getLongMatrixAsList[0] as LongArray
val getLongMatrixAsListType = getLongMatrixAsListRow[0]::class
println(getLongMatrixAsListType)
println(getLongMatrixAsList)
if(getLongMatrixAsListType != Long::class)
	throw Exception("getLongMatrixAsList not Long!")
println("getLong")
if(binding.containsKey("getLong"))
	throw Exception("getLong already defined!")
val getLong: Long = callback("getLong")
val getLongType = getLong::class
println(getLongType)
println(getLong)
if(getLongType != Long::class)
	throw Exception("getLong not Long!")
callback<Unit>("setLong",getLong)

println("getLongVector")
if(binding.containsKey("getLongVector"))
	throw Exception("getLongVector already defined!")
val getLongVector: LongArray = callback("getLongVector")
val getLongVectorType = getLongVector[0]::class
println(getLongVectorType)
println(getLongVector)
if(getLongVectorType != Long::class)
	throw Exception("getLongVector not Long!")
callback<Unit>("setLongVector",getLongVector)

println("getLongVectorAsList")
if(binding.containsKey("getLongVectorAsList"))
	throw Exception("getLongVectorAsList already defined!")
val getLongVectorAsList: LongArray = callback("getLongVectorAsList")
val getLongVectorAsListType = getLongVectorAsList[0]::class
println(getLongVectorAsListType)
println(getLongVectorAsList)
if(getLongVectorAsListType != Long::class)
	throw Exception("getLongVectorAsList not Long!")
callback<Unit>("setLongVectorAsList",getLongVectorAsList)

println("getLongMatrix")
if(binding.containsKey("getLongMatrix"))
	throw Exception("getLongMatrix already defined!")
val getLongMatrix: Array<LongArray> = callback("getLongMatrix")
val getLongMatrixType = getLongMatrix[0][0]::class
println(getLongMatrixType)
println(getLongMatrix)
if(getLongMatrixType != Long::class)
	throw Exception("getLongMatrix not Long!")
callback<Unit>("setLongMatrix",getLongMatrix)

println("getLongMatrixAsList")
if(binding.containsKey("getLongMatrixAsList"))
	throw Exception("getLongMatrixAsList already defined!")
val getLongMatrixAsList: Array<LongArray> = callback("getLongMatrixAsList")
val getLongMatrixAsListType = getLongMatrixAsList[0][0]::class
println(getLongMatrixAsListType)
println(getLongMatrixAsList)
if(getLongMatrixAsListType != Long::class)
	throw Exception("getLongMatrixAsList not Long!")
callback<Unit>("setLongMatrixAsList",getLongMatrixAsList)

println("getDouble")
if(binding.containsKey("getDouble"))
	throw Exception("getDouble already defined!")
val getDouble = putDouble
val getDoubleType = getDouble::class
println(getDoubleType)
println(getDouble)
if(getDoubleType != Double::class)
	throw Exception("getDouble not Double!")
if(!getDouble.isNaN())
	throw Exception("getDouble not NaN!")

println("getDoubleVector")
if(binding.containsKey("getDoubleVector"))
	throw Exception("getDoubleVector already defined!")
val getDoubleVector = putDoubleVector
val getDoubleVectorType = getDoubleVector[0]::class
println(getDoubleVectorType)
println(getDoubleVector)
if(getDoubleVectorType != Double::class)
	throw Exception("getDoubleVector not Double!")
if(!getDoubleVector[1].isNaN())
	throw Exception("getDoubleVector[1] not NaN!")

println("getDoubleVectorAsList")
if(binding.containsKey("getDoubleVectorAsList"))
	throw Exception("getDoubleVectorAsList already defined!")
val getDoubleVectorAsList = putDoubleVectorAsList
val getDoubleVectorAsListType = getDoubleVectorAsList[0]::class
println(getDoubleVectorAsListType)
println(getDoubleVectorAsList)
if(getDoubleVectorAsListType != Double::class)
	throw Exception("getDoubleVectorAsList not Double!")
if(!getDoubleVectorAsList[1].isNaN())
	throw Exception("getDoubleVectorAsList[1] not NaN!")

println("getDoubleMatrix")
if(binding.containsKey("getDoubleMatrix"))
	throw Exception("getDoubleMatrix already defined!")
val getDoubleMatrix = putDoubleMatrix as Array<DoubleArray>
val getDoubleMatrixType = getDoubleMatrix[0][0]::class
println(getDoubleMatrixType)
println(getDoubleMatrix)
if(getDoubleMatrixType != Double::class)
	throw Exception("getDoubleMatrix not Double!")
if(!getDoubleMatrix[0][0].isNaN())
	throw Exception("getDoubleMatrix[0][0] not NaN!")
if(!getDoubleMatrix[1][1].isNaN())
	throw Exception("getDoubleMatrix[1][1] not NaN!")
if(!getDoubleMatrix[2][2].isNaN())
	throw Exception("getDoubleMatrix[2][2] not NaN!")

println("getDoubleMatrixAsList")
if(binding.containsKey("getDoubleMatrixAsList"))
	throw Exception("getDoubleMatrixAsList already defined!")
val getDoubleMatrixAsList = putDoubleMatrixAsList as Array<DoubleArray>
val getDoubleMatrixAsListType = getDoubleMatrixAsList[0][0]::class
println(getDoubleMatrixAsListType)
println(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != Double::class)
	throw Exception("getDoubleMatrixAsList not Double!")
if(!getDoubleMatrixAsList[0][0].isNaN())
	throw Exception("getDoubleMatrixAsList[0][0] not NaN!")
if(!getDoubleMatrixAsList[1][1].isNaN())
	throw Exception("getDoubleMatrixAsList[1][1] not NaN!")
if(!getDoubleMatrixAsList[2][2].isNaN())
	throw Exception("getDoubleMatrixAsList[2][2] not NaN!")

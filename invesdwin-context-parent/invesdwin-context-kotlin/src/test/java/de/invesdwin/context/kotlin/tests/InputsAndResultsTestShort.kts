println("getShort")
if(binding.containsKey("getShort"))
	throw Exception("getShort already defined!")
val getShort = putShort
val getShortType = getShort::class
println(getShortType)
println(getShort)
if(getShortType != Short::class)
	throw Exception("getShort not Short!")

println("getShortVector")
if(binding.containsKey("getShortVector"))
	throw Exception("getShortVector already defined!")
val getShortVector = putShortVector
val getShortVectorType = getShortVector[0]::class
println(getShortVectorType)
println(getShortVector)
if(getShortVectorType != Short::class)
	throw Exception("getShortVector not Short!")

println("getShortVectorAsList")
if(binding.containsKey("getShortVectorAsList"))
	throw Exception("getShortVectorAsList already defined!")
val getShortVectorAsList = putShortVectorAsList
val getShortVectorAsListType = getShortVectorAsList[0]::class
println(getShortVectorAsListType)
println(getShortVectorAsList)
if(getShortVectorAsListType != Short::class)
	throw Exception("getShortVectorAsList not Short!")

println("getShortMatrix")
if(binding.containsKey("getShortMatrix"))
	throw Exception("getShortMatrix already defined!")
val getShortMatrix = putShortMatrix as Array<ShortArray>
val getShortMatrixType = getShortMatrix[0][0]::class
println(getShortMatrixType)
println(getShortMatrix)
if(getShortMatrixType != Short::class)
	throw Exception("getShortMatrix not Short!")

println("getShortMatrixAsList")
if(binding.containsKey("getShortMatrixAsList"))
	throw Exception("getShortMatrixAsList already defined!")
val getShortMatrixAsList = putShortMatrixAsList as Array<ShortArray>
val getShortMatrixAsListType = getShortMatrixAsList[0][0]::class
println(getShortMatrixAsListType)
println(getShortMatrixAsList)
if(getShortMatrixAsListType != Short::class)
	throw Exception("getShortMatrixAsList not Short!")

println("getShort")
if(binding.hasVariable("getShort"))
	throw new Exception("getShort already defined!")
val getShort = putShort
val getShortType = getShort.getClass()
println(getShortType)
println(getShort)
if(getShortType != Short.class)
	throw new Exception("getShort not Short!")

println("getShortVector")
if(binding.hasVariable("getShortVector"))
	throw new Exception("getShortVector already defined!")
val getShortVector = putShortVector
val getShortVectorType = getShortVector[0].getClass()
println(getShortVectorType)
println(getShortVector)
if(getShortVectorType != Short.class)
	throw new Exception("getShortVector not Short!")

println("getShortVectorAsList")
if(binding.hasVariable("getShortVectorAsList"))
	throw new Exception("getShortVectorAsList already defined!")
val getShortVectorAsList = putShortVectorAsList
val getShortVectorAsListType = getShortVectorAsList[0].getClass()
println(getShortVectorAsListType)
println(getShortVectorAsList)
if(getShortVectorAsListType != Short.class)
	throw new Exception("getShortVectorAsList not Short!")

println("getShortMatrix")
if(binding.hasVariable("getShortMatrix"))
	throw new Exception("getShortMatrix already defined!")
val getShortMatrix = putShortMatrix
val getShortMatrixType = getShortMatrix[0][0].getClass()
println(getShortMatrixType)
println(getShortMatrix)
if(getShortMatrixType != Short.class)
	throw new Exception("getShortMatrix not Short!")

println("getShortMatrixAsList")
if(binding.hasVariable("getShortMatrixAsList"))
	throw new Exception("getShortMatrixAsList already defined!")
val getShortMatrixAsList = putShortMatrixAsList
val getShortMatrixAsListType = getShortMatrixAsList[0][0].getClass()
println(getShortMatrixAsListType)
println(getShortMatrixAsList)
if(getShortMatrixAsListType != Short.class)
	throw new Exception("getShortMatrixAsList not Short!")

println("getDecimal")
if(binding.hasVariable("getDecimal"))
	throw new Exception("getDecimal already defined!")
val getDecimal = putDecimal
val getDecimalType = getDecimal.getClass()
println(getDecimalType)
println(getDecimal)
if(getDecimalType != Double.class)
	throw new Exception("getDecimal not Double!")

println("getDecimalVector")
if(binding.hasVariable("getDecimalVector"))
	throw new Exception("getDecimalVector already defined!")
val getDecimalVector = putDecimalVector
val getDecimalVectorType = getDecimalVector[0].getClass()
println(getDecimalVectorType)
println(getDecimalVector)
if(getDecimalVectorType != Double.class)
	throw new Exception("getDecimalVector not Double!")

println("getDecimalVectorAsList")
if(binding.hasVariable("getDecimalVectorAsList"))
	throw new Exception("getDecimalVectorAsList already defined!")
val getDecimalVectorAsList = putDecimalVectorAsList
val getDecimalVectorAsListType = getDecimalVectorAsList[0].getClass()
println(getDecimalVectorAsListType)
println(getDecimalVectorAsList)
if(getDecimalVectorAsListType != Double.class)
	throw new Exception("getDecimalVectorAsList not Double!")

println("getDecimalMatrix")
if(binding.hasVariable("getDecimalMatrix"))
	throw new Exception("getDecimalMatrix already defined!")
val getDecimalMatrix = putDecimalMatrix
val getDecimalMatrixType = getDecimalMatrix[0][0].getClass()
println(getDecimalMatrixType)
println(getDecimalMatrix)
if(getDecimalMatrixType != Double.class)
	throw new Exception("getDecimalMatrix not Double!")

println("getDecimalMatrixAsList")
if(binding.hasVariable("getDecimalMatrixAsList"))
	throw new Exception("getDecimalMatrixAsList already defined!")
val getDecimalMatrixAsList = putDecimalMatrixAsList
val getDecimalMatrixAsListType = getDecimalMatrixAsList[0][0].getClass()
println(getDecimalMatrixAsListType)
println(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != Double.class)
	throw new Exception("getDecimalMatrixAsList not Double!")

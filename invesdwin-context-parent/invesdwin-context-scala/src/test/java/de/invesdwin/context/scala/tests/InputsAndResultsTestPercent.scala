println("getPercent")
if(binding.hasVariable("getPercent"))
	throw new Exception("getPercent already defined!")
val getPercent = putPercent
val getPercentType = getPercent.getClass()
println(getPercentType)
println(getPercent)
if(getPercentType != Double.class)
	throw new Exception("getPercent not Double!")

println("getPercentVector")
if(binding.hasVariable("getPercentVector"))
	throw new Exception("getPercentVector already defined!")
val getPercentVector = putPercentVector
val getPercentVectorType = getPercentVector[0].getClass()
println(getPercentVectorType)
println(getPercentVector)
if(getPercentVectorType != Double.class)
	throw new Exception("getPercentVector not Double!")

println("getPercentVectorAsList")
if(binding.hasVariable("getPercentVectorAsList"))
	throw new Exception("getPercentVectorAsList already defined!")
val getPercentVectorAsList = putPercentVectorAsList
val getPercentVectorAsListType = getPercentVectorAsList[0].getClass()
println(getPercentVectorAsListType)
println(getPercentVectorAsList)
if(getPercentVectorAsListType != Double.class)
	throw new Exception("getPercentVectorAsList not Double!")

println("getPercentMatrix")
if(binding.hasVariable("getPercentMatrix"))
	throw new Exception("getPercentMatrix already defined!")
val getPercentMatrix = putPercentMatrix
val getPercentMatrixType = getPercentMatrix[0][0].getClass()
println(getPercentMatrixType)
println(getPercentMatrix)
if(getPercentMatrixType != Double.class)
	throw new Exception("getPercentMatrix not Double!")

println("getPercentMatrixAsList")
if(binding.hasVariable("getPercentMatrixAsList"))
	throw new Exception("getPercentMatrixAsList already defined!")
val getPercentMatrixAsList = putPercentMatrixAsList
val getPercentMatrixAsListType = getPercentMatrixAsList[0][0].getClass()
println(getPercentMatrixAsListType)
println(getPercentMatrixAsList)
if(getPercentMatrixAsListType != Double.class)
	throw new Exception("getPercentMatrixAsList not Double!")

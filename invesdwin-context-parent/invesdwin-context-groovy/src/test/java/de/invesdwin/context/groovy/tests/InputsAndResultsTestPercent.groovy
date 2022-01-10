println("getPercent")
if(binding.hasVariable('getPercent'))
	throw new Exception("getPercent already defined!")
getPercent = putPercent
getPercentType = getPercent.getClass()
println(getPercentType)
println(getPercent)
if(getPercentType != Double.class)
	throw new Exception("getPercent not Double!")

println("getPercentVector")
if(binding.hasVariable('getPercentVector'))
	throw new Exception("getPercentVector already defined!")
getPercentVector = putPercentVector
getPercentVectorType = getPercentVector[0].getClass()
println(getPercentVectorType)
println(getPercentVector)
if(getPercentVectorType != Double.class)
	throw new Exception("getPercentVector not Double!")

println("getPercentVectorAsList")
if(binding.hasVariable('getPercentVectorAsList'))
	throw new Exception("getPercentVectorAsList already defined!")
getPercentVectorAsList = putPercentVectorAsList
getPercentVectorAsListType = getPercentVectorAsList[0].getClass()
println(getPercentVectorAsListType)
println(getPercentVectorAsList)
if(getPercentVectorAsListType != Double.class)
	throw new Exception("getPercentVectorAsList not Double!")

println("getPercentMatrix")
if(binding.hasVariable('getPercentMatrix'))
	throw new Exception("getPercentMatrix already defined!")
getPercentMatrix = putPercentMatrix
getPercentMatrixType = getPercentMatrix[0][0].getClass()
println(getPercentMatrixType)
println(getPercentMatrix)
if(getPercentMatrixType != Double.class)
	throw new Exception("getPercentMatrix not Double!")

println("getPercentMatrixAsList")
if(binding.hasVariable('getPercentMatrixAsList'))
	throw new Exception("getPercentMatrixAsList already defined!")
getPercentMatrixAsList = putPercentMatrixAsList
getPercentMatrixAsListType = getPercentMatrixAsList[0][0].getClass()
println(getPercentMatrixAsListType)
println(getPercentMatrixAsList)
if(getPercentMatrixAsListType != Double.class)
	throw new Exception("getPercentMatrixAsList not Double!")

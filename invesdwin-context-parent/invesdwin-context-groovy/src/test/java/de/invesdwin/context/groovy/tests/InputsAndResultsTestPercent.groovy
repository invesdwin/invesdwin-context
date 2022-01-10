println("getPercent")
if(binding.hasVariable('getPercent'))
	throw new Exception("getPercent already defined!")
getPercent = putPercent
getPercentType = getPercent.getClass()
println(getPercentType)
println(getPercent)
if(getPercentType is not float):
	throw new Exception("getPercent not float!")

println("getPercentVector")
if(binding.hasVariable('getPercentVector'))
	throw new Exception("getPercentVector already defined!")
getPercentVector = putPercentVector
getPercentVectorType = getPercentVector[0].getClass()
println(getPercentVectorType)
println(getPercentVector)
if(getPercentVectorType is not float):
	throw new Exception("getPercentVector not float!")

println("getPercentVectorAsList")
if(binding.hasVariable('getPercentVectorAsList'))
	throw new Exception("getPercentVectorAsList already defined!")
getPercentVectorAsList = putPercentVectorAsList
getPercentVectorAsListType = getPercentVectorAsList[0].getClass()
println(getPercentVectorAsListType)
println(getPercentVectorAsList)
if(getPercentVectorAsListType is not float):
	throw new Exception("getPercentVectorAsList not float!")

println("getPercentMatrix")
if(binding.hasVariable('getPercentMatrix'))
	throw new Exception("getPercentMatrix already defined!")
getPercentMatrix = putPercentMatrix
getPercentMatrixType = getPercentMatrix[0][0].getClass()
println(getPercentMatrixType)
println(getPercentMatrix)
if(getPercentMatrixType is not float):
	throw new Exception("getPercentMatrix not float!")

println("getPercentMatrixAsList")
if(binding.hasVariable('getPercentMatrixAsList'))
	throw new Exception("getPercentMatrixAsList already defined!")
getPercentMatrixAsList = putPercentMatrixAsList
getPercentMatrixAsListType = getPercentMatrixAsList[0][0].getClass()
println(getPercentMatrixAsListType)
println(getPercentMatrixAsList)
if(getPercentMatrixAsListType is not float):
	throw new Exception("getPercentMatrixAsList not float!")

println("getBoolean")
if(binding.hasVariable('getBoolean'))
	throw new Exception("getBoolean already defined!")
getBoolean = callback("getBoolean")
getBooleanType = getBoolean.getClass()
println(getBooleanType)
println(getBoolean)
if(getBooleanType != Boolean.class)
	throw new Exception("getBoolean not Boolean!")
callback("setBoolean",getBoolean)

println("getBooleanVector")
if(binding.hasVariable('getBooleanVector'))
	throw new Exception("getBooleanVector already defined!")
getBooleanVector = callback("getBooleanVector")
getBooleanVectorType = getBooleanVector[0].getClass()
println(getBooleanVectorType)
println(getBooleanVector)
if(getBooleanVectorType != Boolean.class)
	throw new Exception("getBooleanVector not Boolean!")
callback("setBooleanVector",getBooleanVector)

println("getBooleanVectorAsList")
if(binding.hasVariable('getBooleanVectorAsList'))
	throw new Exception("getBooleanVectorAsList already defined!")
getBooleanVectorAsList = callback("getBooleanVectorAsList")
getBooleanVectorAsListType = getBooleanVectorAsList[0].getClass()
println(getBooleanVectorAsListType)
println(getBooleanVectorAsList)
if(getBooleanVectorAsListType != Boolean.class)
	throw new Exception("getBooleanVectorAsList not Boolean!")
callback("setBooleanVectorAsList",getBooleanVectorAsList)

println("getBooleanMatrix")
if(binding.hasVariable('getBooleanMatrix'))
	throw new Exception("getBooleanMatrix already defined!")
getBooleanMatrix = callback("getBooleanMatrix")
getBooleanMatrixType = getBooleanMatrix[0][0].getClass()
println(getBooleanMatrixType)
println(getBooleanMatrix)
if(getBooleanMatrixType != Boolean.class)
	throw new Exception("getBooleanMatrix not Boolean!")
callback("setBooleanMatrix",new Object[]{getBooleanMatrix})

println("getBooleanMatrixAsList")
if(binding.hasVariable('getBooleanMatrixAsList'))
	throw new Exception("getBooleanMatrixAsList already defined!")
getBooleanMatrixAsList = callback("getBooleanMatrixAsList")
getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].getClass()
println(getBooleanMatrixAsListType)
println(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != Boolean.class)
	throw new Exception("getBooleanMatrixAsList not Boolean!")
callback("setBooleanMatrixAsList",new Object[]{getBooleanMatrixAsList})

println("getByte")
if(binding.hasVariable('getByte'))
	throw new Exception("getByte already defined!")
getByte = callback("getByte")
getByteType = getByte.getClass()
println(getByteType)
println(getByte)
if(getByteType != Byte.class)
	throw new Exception("getByte not Byte!")
callback("setByte",getByte)

println("getByteVector")
if(binding.hasVariable('getByteVector'))
	throw new Exception("getByteVector already defined!")
getByteVector = callback("getByteVector")
getByteVectorType = getByteVector[0].getClass()
println(getByteVectorType)
println(getByteVector)
if(getByteVectorType != Byte.class)
	throw new Exception("getByteVector not Byte!")
callback("setByteVector",getByteVector)

println("getByteVectorAsList")
if(binding.hasVariable('getByteVectorAsList'))
	throw new Exception("getByteVectorAsList already defined!")
getByteVectorAsList = callback("getByteVectorAsList")
getByteVectorAsListType = getByteVectorAsList[0].getClass()
println(getByteVectorAsListType)
println(getByteVectorAsList)
if(getByteVectorAsListType != Byte.class)
	throw new Exception("getByteVectorAsList not Byte!")
callback("setByteVectorAsList",getByteVectorAsList)

println("getByteMatrix")
if(binding.hasVariable('getByteMatrix'))
	throw new Exception("getByteMatrix already defined!")
getByteMatrix = callback("getByteMatrix")
getByteMatrixType = getByteMatrix[0][0].getClass()
println(getByteMatrixType)
println(getByteMatrix)
if(getByteMatrixType != Byte.class)
	throw new Exception("getByteMatrix not Byte!")
callback("setByteMatrix",new Object[]{getByteMatrix})

println("getByteMatrixAsList")
if(binding.hasVariable('getByteMatrixAsList'))
	throw new Exception("getByteMatrixAsList already defined!")
getByteMatrixAsList = callback("getByteMatrixAsList")
getByteMatrixAsListType = getByteMatrixAsList[0][0].getClass()
println(getByteMatrixAsListType)
println(getByteMatrixAsList)
if(getByteMatrixAsListType != Byte.class)
	throw new Exception("getByteMatrixAsList not Byte!")
callback("setByteMatrixAsList",new Object[]{getByteMatrixAsList})
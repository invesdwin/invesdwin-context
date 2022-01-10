println("getByte")
if(binding.hasVariable('getByte'))
	throw new Exception("getByte already defined!")
getByte = putByte
getByteType = getByte.getClass()
println(getByteType)
println(getByte)
if(getByteType is not int):
	throw new Exception("getByte not Byte!")

println("getByteVector")
if(binding.hasVariable('getByteVector'))
	throw new Exception("getByteVector already defined!")
getByteVector = putByteVector
getByteVectorType = getByteVector[0].getClass()
println(getByteVectorType)
println(getByteVector)
if(getByteVectorType is not int):
	throw new Exception("getByteVector not Byte!")

println("getByteVectorAsList")
if(binding.hasVariable('getByteVectorAsList'))
	throw new Exception("getByteVectorAsList already defined!")
getByteVectorAsList = putByteVectorAsList
getByteVectorAsListType = getByteVectorAsList[0].getClass()
println(getByteVectorAsListType)
println(getByteVectorAsList)
if(getByteVectorAsListType is not int):
	throw new Exception("getByteVectorAsList not Byte!")

println("getByteMatrix")
if(binding.hasVariable('getByteMatrix'))
	throw new Exception("getByteMatrix already defined!")
getByteMatrix = putByteMatrix
getByteMatrixType = getByteMatrix[0][0].getClass()
println(getByteMatrixType)
println(getByteMatrix)
if(getByteMatrixType is not int):
	throw new Exception("getByteMatrix not Byte!")

println("getByteMatrixAsList")
if(binding.hasVariable('getByteMatrixAsList'))
	throw new Exception("getByteMatrixAsList already defined!")
getByteMatrixAsList = putByteMatrixAsList
getByteMatrixAsListType = getByteMatrixAsList[0][0].getClass()
println(getByteMatrixAsListType)
println(getByteMatrixAsList)
if(getByteMatrixAsListType is not int):
	throw new Exception("getByteMatrixAsList not Byte!")

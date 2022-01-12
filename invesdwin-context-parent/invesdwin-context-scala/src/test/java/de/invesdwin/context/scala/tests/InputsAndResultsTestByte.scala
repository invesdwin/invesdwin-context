println("getByte")
if(binding.hasVariable("getByte"))
	throw new Exception("getByte already defined!")
val getByte = putByte
val getByteType = getByte.getClass()
println(getByteType)
println(getByte)
if(getByteType != Byte.class)
	throw new Exception("getByte not Byte!")

println("getByteVector")
if(binding.hasVariable("getByteVector"))
	throw new Exception("getByteVector already defined!")
val getByteVector = putByteVector
val getByteVectorType = getByteVector[0].getClass()
println(getByteVectorType)
println(getByteVector)
if(getByteVectorType != Byte.class)
	throw new Exception("getByteVector not Byte!")

println("getByteVectorAsList")
if(binding.hasVariable("getByteVectorAsList"))
	throw new Exception("getByteVectorAsList already defined!")
val getByteVectorAsList = putByteVectorAsList
val getByteVectorAsListType = getByteVectorAsList[0].getClass()
println(getByteVectorAsListType)
println(getByteVectorAsList)
if(getByteVectorAsListType != Byte.class)
	throw new Exception("getByteVectorAsList not Byte!")

println("getByteMatrix")
if(binding.hasVariable("getByteMatrix"))
	throw new Exception("getByteMatrix already defined!")
val getByteMatrix = putByteMatrix
val getByteMatrixType = getByteMatrix[0][0].getClass()
println(getByteMatrixType)
println(getByteMatrix)
if(getByteMatrixType != Byte.class)
	throw new Exception("getByteMatrix not Byte!")

println("getByteMatrixAsList")
if(binding.hasVariable("getByteMatrixAsList"))
	throw new Exception("getByteMatrixAsList already defined!")
val getByteMatrixAsList = putByteMatrixAsList
val getByteMatrixAsListType = getByteMatrixAsList[0][0].getClass()
println(getByteMatrixAsListType)
println(getByteMatrixAsList)
if(getByteMatrixAsListType != Byte.class)
	throw new Exception("getByteMatrixAsList not Byte!")

println("getByte")
if(binding.containsKey("getByte"))
	throw Exception("getByte already defined!")
val getByte = putByte
val getByteType = getByte::class
println(getByteType)
println(getByte)
if(getByteType != Byte::class)
	throw Exception("getByte not Byte!")

println("getByteVector")
if(binding.containsKey("getByteVector"))
	throw Exception("getByteVector already defined!")
val getByteVector = putByteVector
val getByteVectorType = getByteVector[0]::class
println(getByteVectorType)
println(getByteVector)
if(getByteVectorType != Byte::class)
	throw Exception("getByteVector not Byte!")

println("getByteVectorAsList")
if(binding.containsKey("getByteVectorAsList"))
	throw Exception("getByteVectorAsList already defined!")
val getByteVectorAsList = putByteVectorAsList
val getByteVectorAsListType = getByteVectorAsList[0]::class
println(getByteVectorAsListType)
println(getByteVectorAsList)
if(getByteVectorAsListType != Byte::class)
	throw Exception("getByteVectorAsList not Byte!")

println("getByteMatrix")
if(binding.containsKey("getByteMatrix"))
	throw Exception("getByteMatrix already defined!")
val getByteMatrix = putByteMatrix as Array<ByteArray>
val getByteMatrixType = getByteMatrix[0][0]::class
println(getByteMatrixType)
println(getByteMatrix)
if(getByteMatrixType != Byte::class)
	throw Exception("getByteMatrix not Byte!")

println("getByteMatrixAsList")
if(binding.containsKey("getByteMatrixAsList"))
	throw Exception("getByteMatrixAsList already defined!")
val getByteMatrixAsList = putByteMatrixAsList as Array<ByteArray>
val getByteMatrixAsListType = getByteMatrixAsList[0][0]::class
println(getByteMatrixAsListType)
println(getByteMatrixAsList)
if(getByteMatrixAsListType != Byte::class)
	throw Exception("getByteMatrixAsList not Byte!")

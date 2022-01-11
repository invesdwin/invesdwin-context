println("getByte")
if(bindings.containsKey("getByte"))
	throw Exception("getByte already defined!")
val getByte = putByte
val getByteType = getByte::class
println(getByteType)
println(getByte)
if(getByteType != Byte::class)
	throw Exception("getByte not Byte!")

println("getByteVector")
if(bindings.containsKey("getByteVector"))
	throw Exception("getByteVector already defined!")
val getByteVector = putByteVector
val getByteVectorType = getByteVector[0]::class
println(getByteVectorType)
println(getByteVector)
if(getByteVectorType != Byte::class)
	throw Exception("getByteVector not Byte!")

println("getByteVectorAsList")
if(bindings.containsKey("getByteVectorAsList"))
	throw Exception("getByteVectorAsList already defined!")
val getByteVectorAsList = putByteVectorAsList
val getByteVectorAsListType = getByteVectorAsList[0]::class
println(getByteVectorAsListType)
println(getByteVectorAsList)
if(getByteVectorAsListType != Byte::class)
	throw Exception("getByteVectorAsList not Byte!")

println("getByteMatrix")
if(bindings.containsKey("getByteMatrix"))
	throw Exception("getByteMatrix already defined!")
val getByteMatrix = putByteMatrix
val getByteMatrixRow = getByteMatrix[0] as ByteArray
val getByteMatrixType = getByteMatrixRow[0]::class
println(getByteMatrixType)
println(getByteMatrix)
if(getByteMatrixType != Byte::class)
	throw Exception("getByteMatrix not Byte!")

println("getByteMatrixAsList")
if(bindings.containsKey("getByteMatrixAsList"))
	throw Exception("getByteMatrixAsList already defined!")
val getByteMatrixAsList = putByteMatrixAsList
val getByteMatrixAsListRow = getByteMatrixAsList[0] as ByteArray
val getByteMatrixAsListType = getByteMatrixAsListRow[0]::class
println(getByteMatrixAsListType)
println(getByteMatrixAsList)
if(getByteMatrixAsListType != Byte::class)
	throw Exception("getByteMatrixAsList not Byte!")

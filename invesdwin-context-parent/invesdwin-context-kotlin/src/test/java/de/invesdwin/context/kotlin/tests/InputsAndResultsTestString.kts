println("getString")
if(bindings.containsKey("getString"))
	throw Exception("getString already defined!")
val getString = putString
val getStringType = getString::class
println(getStringType)
println(getString)
if(getStringType != String::class)
	throw Exception("getString not String!")

println("getStringWithNull")
if(bindings.containsKey("getStringWithNull"))
	throw Exception("getStringWithNull already defined!")
val getStringWithNull = putStringWithNull
val getStringWithNullType = getStringWithNull::class
println(getStringWithNullType)
println(getStringWithNull)
if(getStringWithNull != null)
	throw Exception("getStringWithNull not null!")

println("getStringVector")
if(bindings.containsKey("getStringVector"))
	throw Exception("getStringVector already defined!")
val getStringVector = putStringVector
val getStringVectorType = getStringVector[0]::class
println(getStringVectorType)
println(getStringVector)
if(getStringVectorType != String::class)
	throw Exception("getStringVector not String!")

println("getStringVectorWithNull")
if(bindings.containsKey("getStringVectorWithNull"))
	throw Exception("getStringVectorWithNull already defined!")
val getStringVectorWithNull = putStringVectorWithNull
val getStringVectorWithNullType = getStringVectorWithNull[0]::class
println(getStringVectorWithNullType)
println(getStringVectorWithNull)
if(getStringVectorWithNullType != String::class)
	throw Exception("getStringVectorWithNull not String!")
if(getStringVectorWithNull[1] != null)
	throw Exception("getStringVectorWithNull[2] not null!")

println("getStringVectorAsList")
if(bindings.containsKey("getStringVectorAsList"))
	throw Exception("getStringVectorAsList already defined!")
val getStringVectorAsList = putStringVectorAsList
val getStringVectorAsListType = getStringVectorAsList[0]::class
println(getStringVectorAsListType)
println(getStringVectorAsList)
if(getStringVectorAsListType != String::class)
	throw Exception("getStringVectorAsList not String!")

println("getStringVectorAsListWithNull")
if(bindings.containsKey("getStringVectorAsListWithNull"))
	throw Exception("getStringVectorAsListWithNull already defined!")
val getStringVectorAsListWithNull = putStringVectorAsListWithNull
val getStringVectorAsListWithNullType = getStringVectorAsListWithNull[0]::class
println(getStringVectorAsListWithNullType)
println(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType != String::class)
	throw Exception("getStringVectorAsListWithNull not String!")
if(getStringVectorAsListWithNull[1] != null)
	throw Exception("getStringVectorAsListWithNull[1] not null!")

println("getStringMatrix")
if(bindings.containsKey("getStringMatrix"))
	throw Exception("getStringMatrix already defined!")
val getStringMatrix = putStringMatrix
val getStringMatrixType = getStringMatrix[0][0]::class
println(getStringMatrixType)
println(getStringMatrix)
if(getStringMatrixType != String::class)
	throw Exception("getStringMatrix not String!")

println("getStringMatrixWithNull")
if(bindings.containsKey("getStringMatrixWithNull"))
	throw Exception("getStringMatrixWithNull already defined!")
val getStringMatrixWithNull = putStringMatrixWithNull
val getStringMatrixWithNullType = getStringMatrixWithNull[0][1]::class
println(getStringMatrixWithNullType)
println(getStringMatrixWithNull)
if(getStringMatrixWithNullType != String::class)
	throw Exception("getStringMatrixWithNull not String!")
if(getStringMatrixWithNull[0][0] != null)
	throw Exception("getStringMatrixWithNull[0][0] not null!")
if(getStringMatrixWithNull[1][1] != null)
	throw Exception("getStringMatrixWithNull[1][1] not null!")
if(getStringMatrixWithNull[2][2] != null)
	throw Exception("getStringMatrixWithNull[2][2] not null!")

println("getStringMatrixAsList")
if(bindings.containsKey("getStringMatrixAsList"))
	throw Exception("getStringMatrixAsList already defined!")
val getStringMatrixAsList = putStringMatrixAsList
val getStringMatrixAsListType = getStringMatrixAsList[0][0]::class
println(getStringMatrixAsListType)
println(getStringMatrixAsList)
if(getStringMatrixAsListType != String::class)
	throw Exception("getStringMatrixAsList not String!")

println("getStringMatrixAsListWithNull")
if(bindings.containsKey("getStringMatrixAsListWithNull"))
	throw Exception("getStringMatrixAsListWithNull already defined!")
val getStringMatrixAsListWithNull = putStringMatrixAsListWithNull
val getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1]::class
println(getStringMatrixAsListWithNullType)
println(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType != String::class)
	throw Exception("getStringMatrixAsListWithNull not String!")
if(getStringMatrixAsListWithNull[0][0] != null)
	throw Exception("getStringMatrixAsListWithNull[0][0] not null!")
if(getStringMatrixAsListWithNull[1][1] != null)
	throw Exception("getStringMatrixAsListWithNull[1][1] not null!")
if(getStringMatrixAsListWithNull[2][2] != null)
	throw Exception("getStringMatrixAsListWithNull[2][2] not null!")

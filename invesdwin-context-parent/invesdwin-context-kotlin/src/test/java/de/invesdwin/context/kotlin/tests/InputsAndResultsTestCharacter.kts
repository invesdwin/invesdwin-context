println("getCharacter")
if(bindings.containsKey("getCharacter"))
	throw Exception("getCharacter already defined!")
val getCharacter = putCharacter
val getCharacterType = getCharacter::class
println(getCharacterType)
println(getCharacter)
if(getCharacterType != Character::class)
	throw Exception("getCharacter not Character!")

println("getCharacterVector")
if(bindings.containsKey("getCharacterVector"))
	throw Exception("getCharacterVector already defined!")
val getCharacterVector = putCharacterVector
val getCharacterVectorType = getCharacterVector[0]::class
println(getCharacterVectorType)
println(getCharacterVector)
if(getCharacterVectorType != Character::class)
	throw Exception("getCharacterVector not Character!")

println("getCharacterVectorAsList")
if(bindings.containsKey("getCharacterVectorAsList"))
	throw Exception("getCharacterVectorAsList already defined!")
val getCharacterVectorAsList = putCharacterVectorAsList
val getCharacterVectorAsListType = getCharacterVectorAsList[0]::class
println(getCharacterVectorAsListType)
println(getCharacterVectorAsList)
if(getCharacterVectorAsListType != Character::class)
	throw Exception("getCharacterVectorAsList not Character!")

println("getCharacterMatrix")
if(bindings.containsKey("getCharacterMatrix"))
	throw Exception("getCharacterMatrix already defined!")
val getCharacterMatrix = putCharacterMatrix
val getCharacterMatrixType = getCharacterMatrix[0][0]::class
println(getCharacterMatrixType)
println(getCharacterMatrix)
if(getCharacterMatrixType != Character::class)
	throw Exception("getCharacterMatrix not Character!")

println("getCharacterMatrixAsList")
if(bindings.containsKey("getCharacterMatrixAsList"))
	throw Exception("getCharacterMatrixAsList already defined!")
val getCharacterMatrixAsList = putCharacterMatrixAsList
val getCharacterMatrixAsListType = getCharacterMatrixAsList[0][0]::class
println(getCharacterMatrixAsListType)
println(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != Character::class)
	throw Exception("getCharacterMatrixAsList not Character!")
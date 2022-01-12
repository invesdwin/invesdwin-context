println("getCharacter")
if(binding.hasVariable("getCharacter"))
	throw new Exception("getCharacter already defined!")
val getCharacter = putCharacter
val getCharacterType = getCharacter.getClass()
println(getCharacterType)
println(getCharacter)
if(getCharacterType != Character.class)
	throw new Exception("getCharacter not Character!")

println("getCharacterVector")
if(binding.hasVariable("getCharacterVector"))
	throw new Exception("getCharacterVector already defined!")
val getCharacterVector = putCharacterVector
val getCharacterVectorType = getCharacterVector[0].getClass()
println(getCharacterVectorType)
println(getCharacterVector)
if(getCharacterVectorType != Character.class)
	throw new Exception("getCharacterVector not Character!")

println("getCharacterVectorAsList")
if(binding.hasVariable("getCharacterVectorAsList"))
	throw new Exception("getCharacterVectorAsList already defined!")
val getCharacterVectorAsList = putCharacterVectorAsList
val getCharacterVectorAsListType = getCharacterVectorAsList[0].getClass()
println(getCharacterVectorAsListType)
println(getCharacterVectorAsList)
if(getCharacterVectorAsListType != Character.class)
	throw new Exception("getCharacterVectorAsList not Character!")

println("getCharacterMatrix")
if(binding.hasVariable("getCharacterMatrix"))
	throw new Exception("getCharacterMatrix already defined!")
val getCharacterMatrix = putCharacterMatrix
val getCharacterMatrixType = getCharacterMatrix[0][0].getClass()
println(getCharacterMatrixType)
println(getCharacterMatrix)
if(getCharacterMatrixType != Character.class)
	throw new Exception("getCharacterMatrix not Character!")

println("getCharacterMatrixAsList")
if(binding.hasVariable("getCharacterMatrixAsList"))
	throw new Exception("getCharacterMatrixAsList already defined!")
val getCharacterMatrixAsList = putCharacterMatrixAsList
val getCharacterMatrixAsListType = getCharacterMatrixAsList[0][0].getClass()
println(getCharacterMatrixAsListType)
println(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != Character.class)
	throw new Exception("getCharacterMatrixAsList not Character!")
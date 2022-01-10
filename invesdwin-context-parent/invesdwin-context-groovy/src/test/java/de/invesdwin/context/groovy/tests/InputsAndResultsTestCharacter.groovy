println("getCharacter")
if(binding.hasVariable('getCharacter'))
	throw new Exception("getCharacter already defined!")
getCharacter = putCharacter
getCharacterType = getCharacter.getClass()
println(getCharacterType)
println(getCharacter)
if(not isinstance(getCharacter, (unicode, str))):
	throw new Exception("getCharacter not String!")

println("getCharacterVector")
if(binding.hasVariable('getCharacterVector'))
	throw new Exception("getCharacterVector already defined!")
getCharacterVector = putCharacterVector
getCharacterVectorType = getCharacterVector[0].getClass()
println(getCharacterVectorType)
println(getCharacterVector)
if(not isinstance(getCharacterVector[0], (unicode, str))):
	throw new Exception("getCharacterVector not String!")

println("getCharacterVectorAsList")
if(binding.hasVariable('getCharacterVectorAsList'))
	throw new Exception("getCharacterVectorAsList already defined!")
getCharacterVectorAsList = putCharacterVectorAsList
getCharacterVectorAsListType = getCharacterVectorAsList[0].getClass()
println(getCharacterVectorAsListType)
println(getCharacterVectorAsList)
if(not isinstance(getCharacterVectorAsList[0], (unicode, str))):
	throw new Exception("getCharacterVectorAsList not String!")

println("getCharacterMatrix")
if(binding.hasVariable('getCharacterMatrix'))
	throw new Exception("getCharacterMatrix already defined!")
getCharacterMatrix = putCharacterMatrix
getCharacterMatrixType = getCharacterMatrix[0][0].getClass()
println(getCharacterMatrixType)
println(getCharacterMatrix)
if(not isinstance(getCharacterMatrix[0][0], (unicode, str))):
	throw new Exception("getCharacterMatrix not String!")

println("getCharacterMatrixAsList")
if(binding.hasVariable('getCharacterMatrixAsList'))
	throw new Exception("getCharacterMatrixAsList already defined!")
getCharacterMatrixAsList = putCharacterMatrixAsList
getCharacterMatrixAsListType = getCharacterMatrixAsList[0][0].getClass()
println(getCharacterMatrixAsListType)
println(getCharacterMatrixAsList)
if(not isinstance(getCharacterMatrixAsList[0][0], (unicode, str))):
	throw new Exception("getCharacterMatrixAsList not String!")
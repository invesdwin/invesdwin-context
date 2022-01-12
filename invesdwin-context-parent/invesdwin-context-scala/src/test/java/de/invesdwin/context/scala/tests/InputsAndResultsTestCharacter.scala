println("getCharacter")
if(getCharacter != null)
	throw new Exception("getCharacter already defined!")
val getCharacter = putCharacter
val getCharacterType = getCharacter.getClass()
println(getCharacterType)
println(getCharacter)
if(getCharacterType != classOf[java.lang.Character])
	throw new Exception("getCharacter not Character!")

println("getCharacterVector")
if(getCharacterVector != null)
	throw new Exception("getCharacterVector already defined!")
val getCharacterVector = putCharacterVector.asInstanceOf[Array[Char]]
val getCharacterVectorType = getCharacterVector(0).getClass()
println(getCharacterVectorType)
println(getCharacterVector)
if(getCharacterVectorType != classOf[Char])
	throw new Exception("getCharacterVector not Character!")

println("getCharacterVectorAsList")
if(getCharacterVectorAsList != null)
	throw new Exception("getCharacterVectorAsList already defined!")
val getCharacterVectorAsList = putCharacterVectorAsList.asInstanceOf[Array[Char]]
val getCharacterVectorAsListType = getCharacterVectorAsList(0).getClass()
println(getCharacterVectorAsListType)
println(getCharacterVectorAsList)
if(getCharacterVectorAsListType != classOf[Char])
	throw new Exception("getCharacterVectorAsList not Character!")

println("getCharacterMatrix")
if(getCharacterMatrix != null)
	throw new Exception("getCharacterMatrix already defined!")
val getCharacterMatrix = putCharacterMatrix.asInstanceOf[Array[Array[Char]]]
val getCharacterMatrixType = getCharacterMatrix(0)(0).getClass()
println(getCharacterMatrixType)
println(getCharacterMatrix)
if(getCharacterMatrixType != classOf[Char])
	throw new Exception("getCharacterMatrix not Character!")

println("getCharacterMatrixAsList")
if(getCharacterMatrixAsList != null)
	throw new Exception("getCharacterMatrixAsList already defined!")
val getCharacterMatrixAsList = putCharacterMatrixAsList.asInstanceOf[Array[Array[Char]]]
val getCharacterMatrixAsListType = getCharacterMatrixAsList(0)(0).getClass()
println(getCharacterMatrixAsListType)
println(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != classOf[Char])
	throw new Exception("getCharacterMatrixAsList not Character!")
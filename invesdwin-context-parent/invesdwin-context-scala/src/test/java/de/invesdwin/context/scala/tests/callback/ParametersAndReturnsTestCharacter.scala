System.out.println("getCharacter")
if(getCharacter != null)
	throw new Exception("getCharacter already defined!")
val getCharacter: java.lang.Character = callback("getCharacter")
val getCharacterType = getCharacter.getClass()
System.out.println(getCharacterType)
System.out.println(getCharacter)
if(getCharacterType != classOf[java.lang.Character])
	throw new Exception("getCharacter not Character!")
callback("setCharacter",getCharacter)

System.out.println("getCharacterVector")
if(getCharacterVector != null)
	throw new Exception("getCharacterVector already defined!")
val getCharacterVector: Array[Char] = callback("getCharacterVector")
val getCharacterVectorType = getCharacterVector(0).getClass()
System.out.println(getCharacterVectorType)
System.out.println(getCharacterVector)
if(getCharacterVectorType != classOf[Char])
	throw new Exception("getCharacterVector not Character!")
callback("setCharacterVector",getCharacterVector)

System.out.println("getCharacterVectorAsList")
if(getCharacterVectorAsList != null)
	throw new Exception("getCharacterVectorAsList already defined!")
val getCharacterVectorAsList: Array[Char] = callback("getCharacterVectorAsList")
val getCharacterVectorAsListType = getCharacterVectorAsList(0).getClass()
System.out.println(getCharacterVectorAsListType)
System.out.println(getCharacterVectorAsList)
if(getCharacterVectorAsListType != classOf[Char])
	throw new Exception("getCharacterVectorAsList not Character!")
callback("setCharacterVectorAsList",getCharacterVectorAsList)

System.out.println("getCharacterMatrix")
if(getCharacterMatrix != null)
	throw new Exception("getCharacterMatrix already defined!")
val getCharacterMatrix: Array[Array[Char]] = callback("getCharacterMatrix")
val getCharacterMatrixType = getCharacterMatrix(0)(0).getClass()
System.out.println(getCharacterMatrixType)
System.out.println(getCharacterMatrix)
if(getCharacterMatrixType != classOf[Char])
	throw new Exception("getCharacterMatrix not Character!")
callback("setCharacterMatrix",getCharacterMatrix)

System.out.println("getCharacterMatrixAsList")
if(getCharacterMatrixAsList != null)
	throw new Exception("getCharacterMatrixAsList already defined!")
val getCharacterMatrixAsList: Array[Array[Char]] = callback("getCharacterMatrixAsList")
val getCharacterMatrixAsListType = getCharacterMatrixAsList(0)(0).getClass()
System.out.println(getCharacterMatrixAsListType)
System.out.println(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != classOf[Char])
	throw new Exception("getCharacterMatrixAsList not Character!")
callback("setCharacterMatrixAsList",getCharacterMatrixAsList)
	
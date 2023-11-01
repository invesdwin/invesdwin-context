System.out.println("getCharacter");
if(getCharacter != void)
	throw new Exception("getCharacter already defined!");
getCharacter = callback("getCharacter");
getCharacterType = getCharacter.getClass();
System.out.println(getCharacterType);
System.out.println(getCharacter);
if(getCharacterType != Character.class)
	throw new Exception("getCharacter not Character!");
callback("setCharacter",getCharacter);

System.out.println("getCharacterVector");
if(getCharacterVector != void)
	throw new Exception("getCharacterVector already defined!");
getCharacterVector = callback("getCharacterVector");
getCharacterVectorType = getCharacterVector.getClass().getComponentType();
System.out.println(getCharacterVectorType);
System.out.println(getCharacterVector);
if(getCharacterVectorType != char.class)
	throw new Exception("getCharacterVector not Character!");
callback("setCharacterVector",getCharacterVector);

System.out.println("getCharacterVectorAsList");
if(getCharacterVectorAsList != void)
	throw new Exception("getCharacterVectorAsList already defined!");
getCharacterVectorAsList = callback("getCharacterVectorAsList");
getCharacterVectorAsListType = getCharacterVectorAsList.getClass().getComponentType();
System.out.println(getCharacterVectorAsListType);
System.out.println(getCharacterVectorAsList);
if(getCharacterVectorAsListType != char.class)
	throw new Exception("getCharacterVectorAsList not Character!");
callback("setCharacterVectorAsList",getCharacterVectorAsList);

System.out.println("getCharacterMatrix");
if(getCharacterMatrix != void)
	throw new Exception("getCharacterMatrix already defined!");
getCharacterMatrix = callback("getCharacterMatrix");
getCharacterMatrixType = getCharacterMatrix[0].getClass().getComponentType();
System.out.println(getCharacterMatrixType);
System.out.println(getCharacterMatrix);
if(getCharacterMatrixType != char.class)
	throw new Exception("getCharacterMatrix not Character!");
callback("setCharacterMatrix",new Object[]{getCharacterMatrix});

System.out.println("getCharacterMatrixAsList");
if(getCharacterMatrixAsList != void)
	throw new Exception("getCharacterMatrixAsList already defined!");
getCharacterMatrixAsList = callback("getCharacterMatrixAsList");
getCharacterMatrixAsListType = getCharacterMatrixAsList[0].getClass().getComponentType();
System.out.println(getCharacterMatrixAsListType);
System.out.println(getCharacterMatrixAsList);
if(getCharacterMatrixAsListType != char.class)
	throw new Exception("getCharacterMatrixAsList not Character!");
callback("setCharacterMatrixAsList",new Object[]{getCharacterMatrixAsList});
	
System.out.println("getCharacter");
if(bindings.containsKey("getCharacter"))
	throw new Exception("getCharacter already defined!");
var getCharacter = putCharacter;
var getCharacterType = getCharacter.getClass();
System.out.println(getCharacterType);
System.out.println(getCharacter);
if(getCharacterType != Character.class)
	throw new Exception("getCharacter not Character!");

System.out.println("getCharacterVector");
if(bindings.containsKey("getCharacterVector"))
	throw new Exception("getCharacterVector already defined!");
var getCharacterVector = putCharacterVector;
var getCharacterVectorType = getCharacterVector.getClass().getComponentType();
System.out.println(getCharacterVectorType);
System.out.println(getCharacterVector);
if(getCharacterVectorType != char.class)
	throw new Exception("getCharacterVector not Character!");

System.out.println("getCharacterVectorAsList");
if(bindings.containsKey("getCharacterVectorAsList"))
	throw new Exception("getCharacterVectorAsList already defined!");
var getCharacterVectorAsList = putCharacterVectorAsList;
var getCharacterVectorAsListType = getCharacterVectorAsList.getClass().getComponentType();
System.out.println(getCharacterVectorAsListType);
System.out.println(getCharacterVectorAsList);
if(getCharacterVectorAsListType != char.class)
	throw new Exception("getCharacterVectorAsList not Character!");

System.out.println("getCharacterMatrix");
if(bindings.containsKey("getCharacterMatrix"))
	throw new Exception("getCharacterMatrix already defined!");
var getCharacterMatrix = putCharacterMatrix;
var getCharacterMatrixType = getCharacterMatrix[0].getClass().getComponentType();
System.out.println(getCharacterMatrixType);
System.out.println(getCharacterMatrix);
if(getCharacterMatrixType != char.class)
	throw new Exception("getCharacterMatrix not Character!");

System.out.println("getCharacterMatrixAsList");
if(bindings.containsKey("getCharacterMatrixAsList"))
	throw new Exception("getCharacterMatrixAsList already defined!");
var getCharacterMatrixAsList = putCharacterMatrixAsList;
var getCharacterMatrixAsListType = getCharacterMatrixAsList[0].getClass().getComponentType();
System.out.println(getCharacterMatrixAsListType);
System.out.println(getCharacterMatrixAsList);
if(getCharacterMatrixAsListType != char.class)
	throw new Exception("getCharacterMatrixAsList not Character!");
System.out.println("getCharacter");
if(binding.containsKey("getCharacter"))
	throw new Exception("getCharacter already defined!");
var getCharacter = callback("getCharacter");
var getCharacterType = getCharacter.getClass();
System.out.println(getCharacterType);
System.out.println(getCharacter);
if(getCharacterType != Character.class)
	throw new Exception("getCharacter not Character!");
callback("setCharacter",getCharacter);

System.out.println("getCharacterVector");
if(binding.containsKey("getCharacterVector"))
	throw new Exception("getCharacterVector already defined!");
var getCharacterVector = callback("getCharacterVector");
var getCharacterVectorType = getCharacterVector.getClass().getComponentType();
System.out.println(getCharacterVectorType);
System.out.println(getCharacterVector);
if(getCharacterVectorType != char.class)
	throw new Exception("getCharacterVector not Character!");
callback("setCharacterVector",getCharacterVector);

System.out.println("getCharacterVectorAsList");
if(binding.containsKey("getCharacterVectorAsList"))
	throw new Exception("getCharacterVectorAsList already defined!");
var getCharacterVectorAsList = callback("getCharacterVectorAsList");
var getCharacterVectorAsListType = getCharacterVectorAsList.getClass().getComponentType();
System.out.println(getCharacterVectorAsListType);
System.out.println(getCharacterVectorAsList);
if(getCharacterVectorAsListType != char.class)
	throw new Exception("getCharacterVectorAsList not Character!");
callback("setCharacterVectorAsList",getCharacterVectorAsList);

System.out.println("getCharacterMatrix");
if(binding.containsKey("getCharacterMatrix"))
	throw new Exception("getCharacterMatrix already defined!");
var getCharacterMatrix = callback("getCharacterMatrix");
var getCharacterMatrixType = getCharacterMatrix[0].getClass().getComponentType();
System.out.println(getCharacterMatrixType);
System.out.println(getCharacterMatrix);
if(getCharacterMatrixType != char.class)
	throw new Exception("getCharacterMatrix not Character!");
callback("setCharacterMatrix",getCharacterMatrix);

System.out.println("getCharacterMatrixAsList");
if(binding.containsKey("getCharacterMatrixAsList"))
	throw new Exception("getCharacterMatrixAsList already defined!");
var getCharacterMatrixAsList = callback("getCharacterMatrixAsList");
var getCharacterMatrixAsListType = getCharacterMatrixAsList[0].getClass().getComponentType();
System.out.println(getCharacterMatrixAsListType);
System.out.println(getCharacterMatrixAsList);
if(getCharacterMatrixAsListType != char.class)
	throw new Exception("getCharacterMatrixAsList not Character!");
callback("setCharacterMatrixAsList",getCharacterMatrixAsList);
	
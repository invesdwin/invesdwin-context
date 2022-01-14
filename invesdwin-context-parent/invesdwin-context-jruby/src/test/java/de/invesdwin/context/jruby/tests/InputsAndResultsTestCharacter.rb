print("getCharacter")
if (defined?(getCharacter)).nil?
	raise "getCharacter already defined!"
getCharacter = putCharacter
getCharacterType = getCharacter.class
print(getCharacterType)
print(getCharacter)
if(getCharacterType != 'string')
	raise "getCharacter not string!"

print("getCharacterVector")
if (defined?(getCharacterVector)).nil?
	raise "getCharacterVector already defined!"
getCharacterVector = putCharacterVector
getCharacterVectorType = getCharacterVector[0].class
print(getCharacterVectorType)
print(getCharacterVector)
if(getCharacterVectorType != 'string')
	raise "getCharacterVector not string!"

print("getCharacterVectorAsList")
if (defined?(getCharacterVectorAsList)).nil?
	raise "getCharacterVectorAsList already defined!"
getCharacterVectorAsList = putCharacterVectorAsList
getCharacterVectorAsListType = getCharacterVectorAsList[0].class
print(getCharacterVectorAsListType)
print(getCharacterVectorAsList)
if(getCharacterVectorAsListType != 'string')
	raise "getCharacterVectorAsList not string!"

print("getCharacterMatrix")
if (defined?(getCharacterMatrix)).nil?
	raise "getCharacterMatrix already defined!"
getCharacterMatrix = putCharacterMatrix
getCharacterMatrixType = getCharacterMatrix[0][0].class
print(getCharacterMatrixType)
print(getCharacterMatrix)
if(getCharacterMatrixType != 'string')
	raise "getCharacterMatrix not string!"

print("getCharacterMatrixAsList")
if (defined?(getCharacterMatrixAsList)).nil?
	raise "getCharacterMatrixAsList already defined!"
getCharacterMatrixAsList = putCharacterMatrixAsList
getCharacterMatrixAsListType = getCharacterMatrixAsList[0][0].class
print(getCharacterMatrixAsListType)
print(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != 'string')
	raise "getCharacterMatrixAsList not string!"
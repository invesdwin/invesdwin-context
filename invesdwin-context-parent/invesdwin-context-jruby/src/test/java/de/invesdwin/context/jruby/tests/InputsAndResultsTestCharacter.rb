print("getCharacter")
if (defined?(getCharacter)).nil?
	throw "getCharacter already defined!"
getCharacter = putCharacter
getCharacterType = typeof getCharacter
print(getCharacterType)
print(getCharacter)
if(getCharacterType !== 'string')
	throw "getCharacter not string!"

print("getCharacterVector")
if (defined?(getCharacterVector)).nil?
	throw "getCharacterVector already defined!"
getCharacterVector = putCharacterVector
getCharacterVectorType = typeof getCharacterVector[0]
print(getCharacterVectorType)
print(getCharacterVector)
if(getCharacterVectorType !== 'string')
	throw "getCharacterVector not string!"

print("getCharacterVectorAsList")
if (defined?(getCharacterVectorAsList)).nil?
	throw "getCharacterVectorAsList already defined!"
getCharacterVectorAsList = putCharacterVectorAsList
getCharacterVectorAsListType = typeof getCharacterVectorAsList[0]
print(getCharacterVectorAsListType)
print(getCharacterVectorAsList)
if(getCharacterVectorAsListType !== 'string')
	throw "getCharacterVectorAsList not string!"

print("getCharacterMatrix")
if (defined?(getCharacterMatrix)).nil?
	throw "getCharacterMatrix already defined!"
getCharacterMatrix = putCharacterMatrix
getCharacterMatrixType = typeof getCharacterMatrix[0][0]
print(getCharacterMatrixType)
print(getCharacterMatrix)
if(getCharacterMatrixType !== 'string')
	throw "getCharacterMatrix not string!"

print("getCharacterMatrixAsList")
if (defined?(getCharacterMatrixAsList)).nil?
	throw "getCharacterMatrixAsList already defined!"
getCharacterMatrixAsList = putCharacterMatrixAsList
getCharacterMatrixAsListType = typeof getCharacterMatrixAsList[0][0]
print(getCharacterMatrixAsListType)
print(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType !== 'string')
	throw "getCharacterMatrixAsList not string!"
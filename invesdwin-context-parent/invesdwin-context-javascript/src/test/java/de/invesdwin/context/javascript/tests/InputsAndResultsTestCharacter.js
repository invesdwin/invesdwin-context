print("getCharacter")
if(typeof getCharacter !== 'undefined')
	throw "getCharacter already defined!"
getCharacter = putCharacter
getCharacterType = typeof getCharacter
print(getCharacterType)
print(getCharacter)
if(getCharacterType !== 'string')
	throw "getCharacter not string!"

print("getCharacterVector")
if(typeof getCharacterVector !== 'undefined')
	throw "getCharacterVector already defined!"
getCharacterVector = putCharacterVector
getCharacterVectorType = typeof getCharacterVector[0]
print(getCharacterVectorType)
print(getCharacterVector)
if(getCharacterVectorType !== 'string')
	throw "getCharacterVector not string!"

print("getCharacterVectorAsList")
if(typeof getCharacterVectorAsList !== 'undefined')
	throw "getCharacterVectorAsList already defined!"
getCharacterVectorAsList = putCharacterVectorAsList
getCharacterVectorAsListType = typeof getCharacterVectorAsList[0]
print(getCharacterVectorAsListType)
print(getCharacterVectorAsList)
if(getCharacterVectorAsListType !== 'string')
	throw "getCharacterVectorAsList not string!"

print("getCharacterMatrix")
if(typeof getCharacterMatrix !== 'undefined')
	throw "getCharacterMatrix already defined!"
getCharacterMatrix = putCharacterMatrix
getCharacterMatrixType = typeof getCharacterMatrix[0][0]
print(getCharacterMatrixType)
print(getCharacterMatrix)
if(getCharacterMatrixType !== 'string')
	throw "getCharacterMatrix not string!"

print("getCharacterMatrixAsList")
if(typeof getCharacterMatrixAsList !== 'undefined')
	throw "getCharacterMatrixAsList already defined!"
getCharacterMatrixAsList = putCharacterMatrixAsList
getCharacterMatrixAsListType = typeof getCharacterMatrixAsList[0][0]
print(getCharacterMatrixAsListType)
print(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType !== 'string')
	throw "getCharacterMatrixAsList not string!"
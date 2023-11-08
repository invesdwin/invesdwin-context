print("getCharacter")
if(typeof getCharacter !== 'undefined')
	throw "getCharacter already defined!"
getCharacter = callback("getCharacter")
getCharacterType = typeof getCharacter
print(getCharacterType)
print(getCharacter)
if(getCharacterType !== 'string')
	throw "getCharacter not string!"
callback("setCharacter",getCharacter)

print("getCharacterVector")
if(typeof getCharacterVector !== 'undefined')
	throw "getCharacterVector already defined!"
getCharacterVector = callback("getCharacterVector")
getCharacterVectorType = typeof getCharacterVector[0]
print(getCharacterVectorType)
print(getCharacterVector)
if(getCharacterVectorType !== 'string')
	throw "getCharacterVector not string!"
callback("setCharacterVector",getCharacterVector)

print("getCharacterVectorAsList")
if(typeof getCharacterVectorAsList !== 'undefined')
	throw "getCharacterVectorAsList already defined!"
getCharacterVectorAsList = callback("getCharacterVectorAsList")
getCharacterVectorAsListType = typeof getCharacterVectorAsList[0]
print(getCharacterVectorAsListType)
print(getCharacterVectorAsList)
if(getCharacterVectorAsListType !== 'string')
	throw "getCharacterVectorAsList not string!"
callback("setCharacterVectorAsList",getCharacterVectorAsList)

print("getCharacterMatrix")
if(typeof getCharacterMatrix !== 'undefined')
	throw "getCharacterMatrix already defined!"
getCharacterMatrix = callback("getCharacterMatrix")
getCharacterMatrixType = typeof getCharacterMatrix[0][0]
print(getCharacterMatrixType)
print(getCharacterMatrix)
if(getCharacterMatrixType !== 'string')
	throw "getCharacterMatrix not string!"
callback("setCharacterMatrix",getCharacterMatrix)

print("getCharacterMatrixAsList")
if(typeof getCharacterMatrixAsList !== 'undefined')
	throw "getCharacterMatrixAsList already defined!"
getCharacterMatrixAsList = callback("getCharacterMatrixAsList")
getCharacterMatrixAsListType = typeof getCharacterMatrixAsList[0][0]
print(getCharacterMatrixAsListType)
print(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType !== 'string')
	throw "getCharacterMatrixAsList not string!"
callback("setCharacterMatrixAsList",getCharacterMatrixAsList)
	
print("getShort")
if (defined?(getShort)).nil?
	throw "getShort already defined!"
getShort = putShort
getShortType = typeof getShort
print(getShortType)
print(getShort)
if(getShortType !== 'number')
	throw "getShort not number!"

print("getShortVector")
if (defined?(getShortVector)).nil?
	throw "getShortVector already defined!"
getShortVector = putShortVector
getShortVectorType = typeof getShortVector[0]
print(getShortVectorType)
print(getShortVector)
if(getShortVectorType !== 'number')
	throw "getShortVector not number!"

print("getShortVectorAsList")
if (defined?(getShortVectorAsList)).nil?
	throw "getShortVectorAsList already defined!"
getShortVectorAsList = putShortVectorAsList
getShortVectorAsListType = typeof getShortVectorAsList[0]
print(getShortVectorAsListType)
print(getShortVectorAsList)
if(getShortVectorAsListType !== 'number')
	throw "getShortVectorAsList not number!"

print("getShortMatrix")
if (defined?(getShortMatrix)).nil?
	throw "getShortMatrix already defined!"
getShortMatrix = putShortMatrix
getShortMatrixType = typeof getShortMatrix[0][0]
print(getShortMatrixType)
print(getShortMatrix)
if(getShortMatrixType !== 'number')
	throw "getShortMatrix not number!"

print("getShortMatrixAsList")
if (defined?(getShortMatrixAsList)).nil?
	throw "getShortMatrixAsList already defined!"
getShortMatrixAsList = putShortMatrixAsList
getShortMatrixAsListType = typeof getShortMatrixAsList[0][0]
print(getShortMatrixAsListType)
print(getShortMatrixAsList)
if(getShortMatrixAsListType !== 'number')
	throw "getShortMatrixAsList not number!"

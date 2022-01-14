print("getInteger")
if (defined?(getInteger)).nil?
	throw "getInteger already defined!"
getInteger = putInteger
getIntegerType = typeof getInteger
print(getIntegerType)
print(getInteger)
if(getIntegerType !== 'number')
	throw "getInteger not number!"

print("getIntegerVector")
if (defined?(getIntegerVector)).nil?
	throw "getIntegerVector already defined!"
getIntegerVector = putIntegerVector
getIntegerVectorType = typeof getIntegerVector[0]
print(getIntegerVectorType)
print(getIntegerVector)
if(getIntegerVectorType !== 'number')
	throw "getIntegerVector not number!"

print("getIntegerVectorAsList")
if (defined?(getIntegerVectorAsList)).nil?
	throw "getIntegerVectorAsList already defined!"
getIntegerVectorAsList = putIntegerVectorAsList
getIntegerVectorAsListType = typeof getIntegerVectorAsList[0]
print(getIntegerVectorAsListType)
print(getIntegerVectorAsList)
if(getIntegerVectorAsListType !== 'number')
	throw "getIntegerVectorAsList not number!"

print("getIntegerMatrix")
if (defined?(getIntegerMatrix)).nil?
	throw "getIntegerMatrix already defined!"
getIntegerMatrix = putIntegerMatrix
getIntegerMatrixType = typeof getIntegerMatrix[0][0]
print(getIntegerMatrixType)
print(getIntegerMatrix)
if(getIntegerMatrixType !== 'number')
	throw "getIntegerMatrix not number!"

print("getIntegerMatrixAsList")
if (defined?(getIntegerMatrixAsList)).nil?
	throw "getIntegerMatrixAsList already defined!"
getIntegerMatrixAsList = putIntegerMatrixAsList
getIntegerMatrixAsListType = typeof getIntegerMatrixAsList[0][0]
print(getIntegerMatrixAsListType)
print(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType !== 'number')
	throw "getIntegerMatrixAsList not number!"

print("getInteger")
if (defined?(getInteger)).nil?
	raise "getInteger already defined!"
getInteger = putInteger
getIntegerType = getInteger.class
print(getIntegerType)
print(getInteger)
if(getIntegerType != 'number')
	raise "getInteger not number!"

print("getIntegerVector")
if (defined?(getIntegerVector)).nil?
	raise "getIntegerVector already defined!"
getIntegerVector = putIntegerVector
getIntegerVectorType = getIntegerVector[0].class
print(getIntegerVectorType)
print(getIntegerVector)
if(getIntegerVectorType != 'number')
	raise "getIntegerVector not number!"

print("getIntegerVectorAsList")
if (defined?(getIntegerVectorAsList)).nil?
	raise "getIntegerVectorAsList already defined!"
getIntegerVectorAsList = putIntegerVectorAsList
getIntegerVectorAsListType = getIntegerVectorAsList[0].class
print(getIntegerVectorAsListType)
print(getIntegerVectorAsList)
if(getIntegerVectorAsListType != 'number')
	raise "getIntegerVectorAsList not number!"

print("getIntegerMatrix")
if (defined?(getIntegerMatrix)).nil?
	raise "getIntegerMatrix already defined!"
getIntegerMatrix = putIntegerMatrix
getIntegerMatrixType = getIntegerMatrix[0][0].class
print(getIntegerMatrixType)
print(getIntegerMatrix)
if(getIntegerMatrixType != 'number')
	raise "getIntegerMatrix not number!"

print("getIntegerMatrixAsList")
if (defined?(getIntegerMatrixAsList)).nil?
	raise "getIntegerMatrixAsList already defined!"
getIntegerMatrixAsList = putIntegerMatrixAsList
getIntegerMatrixAsListType = getIntegerMatrixAsList[0][0].class
print(getIntegerMatrixAsListType)
print(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType != 'number')
	raise "getIntegerMatrixAsList not number!"

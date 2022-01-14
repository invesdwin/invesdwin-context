print("getShort")
if (defined?(getShort)).nil?
	raise "getShort already defined!"
getShort = putShort
getShortType = getShort.class
print(getShortType)
print(getShort)
if(getShortType != 'number')
	raise "getShort not number!"

print("getShortVector")
if (defined?(getShortVector)).nil?
	raise "getShortVector already defined!"
getShortVector = putShortVector
getShortVectorType = getShortVector[0].class
print(getShortVectorType)
print(getShortVector)
if(getShortVectorType != 'number')
	raise "getShortVector not number!"

print("getShortVectorAsList")
if (defined?(getShortVectorAsList)).nil?
	raise "getShortVectorAsList already defined!"
getShortVectorAsList = putShortVectorAsList
getShortVectorAsListType = getShortVectorAsList[0].class
print(getShortVectorAsListType)
print(getShortVectorAsList)
if(getShortVectorAsListType != 'number')
	raise "getShortVectorAsList not number!"

print("getShortMatrix")
if (defined?(getShortMatrix)).nil?
	raise "getShortMatrix already defined!"
getShortMatrix = putShortMatrix
getShortMatrixType = getShortMatrix[0][0].class
print(getShortMatrixType)
print(getShortMatrix)
if(getShortMatrixType != 'number')
	raise "getShortMatrix not number!"

print("getShortMatrixAsList")
if (defined?(getShortMatrixAsList)).nil?
	raise "getShortMatrixAsList already defined!"
getShortMatrixAsList = putShortMatrixAsList
getShortMatrixAsListType = getShortMatrixAsList[0][0].class
print(getShortMatrixAsListType)
print(getShortMatrixAsList)
if(getShortMatrixAsListType != 'number')
	raise "getShortMatrixAsList not number!"

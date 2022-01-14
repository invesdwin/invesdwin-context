print("getDouble")
if (defined?(getDouble)).nil?
	raise "getDouble already defined!"
getDouble = putDouble
getDoubleType = getDouble.class
print(getDoubleType)
print(getDouble)
if(getDoubleType != 'number')
	raise "getDouble not number!"

print("getDoubleVector")
if (defined?(getDoubleVector)).nil?
	raise "getDoubleVector already defined!"
getDoubleVector = putDoubleVector
getDoubleVectorType = getDoubleVector[0].class
print(getDoubleVectorType)
print(getDoubleVector)
if(getDoubleVectorType != 'number')
	raise "getDoubleVector not number!"

print("getDoubleVectorAsList")
if (defined?(getDoubleVectorAsList)).nil?
	raise "getDoubleVectorAsList already defined!"
getDoubleVectorAsList = putDoubleVectorAsList
getDoubleVectorAsListType = getDoubleVectorAsList[0].class
print(getDoubleVectorAsListType)
print(getDoubleVectorAsList)
if(getDoubleVectorAsListType != 'number')
	raise "getDoubleVectorAsList not number!"

print("getDoubleMatrix")
if (defined?(getDoubleMatrix)).nil?
	raise "getDoubleMatrix already defined!"
getDoubleMatrix = putDoubleMatrix
getDoubleMatrixType = getDoubleMatrix[0][0].class
print(getDoubleMatrixType)
print(getDoubleMatrix)
if(getDoubleMatrixType != 'number')
	raise "getDoubleMatrix not number!"

print("getDoubleMatrixAsList")
if (defined?(getDoubleMatrixAsList)).nil?
	raise "getDoubleMatrixAsList already defined!"
getDoubleMatrixAsList = putDoubleMatrixAsList
getDoubleMatrixAsListType = getDoubleMatrixAsList[0][0].class
print(getDoubleMatrixAsListType)
print(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != 'number')
	raise "getDoubleMatrixAsList not number!"

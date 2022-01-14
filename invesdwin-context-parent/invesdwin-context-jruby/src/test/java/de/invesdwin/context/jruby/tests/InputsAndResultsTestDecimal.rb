print("getDecimal")
if (defined?(getDecimal)).nil?
	raise "getDecimal already defined!"
getDecimal = putDecimal
getDecimalType = getDecimal.class
print(getDecimalType)
print(getDecimal)
if(getDecimalType != 'number')
	raise "getDecimal not number!"

print("getDecimalVector")
if (defined?(getDecimalVector)).nil?
	raise "getDecimalVector already defined!"
getDecimalVector = putDecimalVector
getDecimalVectorType = getDecimalVector[0].class
print(getDecimalVectorType)
print(getDecimalVector)
if(getDecimalVectorType != 'number')
	raise "getDecimalVector not number!"

print("getDecimalVectorAsList")
if (defined?(getDecimalVectorAsList)).nil?
	raise "getDecimalVectorAsList already defined!"
getDecimalVectorAsList = putDecimalVectorAsList
getDecimalVectorAsListType = getDecimalVectorAsList[0].class
print(getDecimalVectorAsListType)
print(getDecimalVectorAsList)
if(getDecimalVectorAsListType != 'number')
	raise "getDecimalVectorAsList not number!"

print("getDecimalMatrix")
if (defined?(getDecimalMatrix)).nil?
	raise "getDecimalMatrix already defined!"
getDecimalMatrix = putDecimalMatrix
getDecimalMatrixType = getDecimalMatrix[0][0].class
print(getDecimalMatrixType)
print(getDecimalMatrix)
if(getDecimalMatrixType != 'number')
	raise "getDecimalMatrix not number!"

print("getDecimalMatrixAsList")
if (defined?(getDecimalMatrixAsList)).nil?
	raise "getDecimalMatrixAsList already defined!"
getDecimalMatrixAsList = putDecimalMatrixAsList
getDecimalMatrixAsListType = getDecimalMatrixAsList[0][0].class
print(getDecimalMatrixAsListType)
print(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != 'number')
	raise "getDecimalMatrixAsList not number!"

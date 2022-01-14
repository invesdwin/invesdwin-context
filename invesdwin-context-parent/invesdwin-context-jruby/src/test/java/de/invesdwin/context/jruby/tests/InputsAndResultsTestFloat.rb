print("getFloat")
if (defined?(getFloat)).nil?
	raise "getFloat already defined!"
getFloat = putFloat
getFloatType = getFloat.class
print(getFloatType)
print(getFloat)
if(getFloatType != 'number')
	raise "getFloat not number!"

print("getFloatVector")
if (defined?(getFloatVector)).nil?
	raise "getFloatVector already defined!"
getFloatVector = putFloatVector
getFloatVectorType = getFloatVector[0].class
print(getFloatVectorType)
print(getFloatVector)
if(getFloatVectorType != 'number')
	raise "getFloatVector not number!"

print("getFloatVectorAsList")
if (defined?(getFloatVectorAsList)).nil?
	raise "getFloatVectorAsList already defined!"
getFloatVectorAsList = putFloatVectorAsList
getFloatVectorAsListType = getFloatVectorAsList[0].class
print(getFloatVectorAsListType)
print(getFloatVectorAsList)
if(getFloatVectorAsListType != 'number')
	raise "getFloatVectorAsList not number!"

print("getFloatMatrix")
if (defined?(getFloatMatrix)).nil?
	raise "getFloatMatrix already defined!"
getFloatMatrix = putFloatMatrix
getFloatMatrixType = getFloatMatrix[0][0].class
print(getFloatMatrixType)
print(getFloatMatrix)
if(getFloatMatrixType != 'number')
	raise "getFloatMatrix not number!"

print("getFloatMatrixAsList")
if (defined?(getFloatMatrixAsList)).nil?
	raise "getFloatMatrixAsList already defined!"
getFloatMatrixAsList = putFloatMatrixAsList
getFloatMatrixAsListType = getFloatMatrixAsList[0][0].class
print(getFloatMatrixAsListType)
print(getFloatMatrixAsList)
if(getFloatMatrixAsListType != 'number')
	raise "getFloatMatrixAsList not number!"

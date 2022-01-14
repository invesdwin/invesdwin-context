print("getFloat")
if (defined?(getFloat)).nil?
	throw "getFloat already defined!"
getFloat = putFloat
getFloatType = typeof getFloat
print(getFloatType)
print(getFloat)
if(getFloatType !== 'number')
	throw "getFloat not number!"

print("getFloatVector")
if (defined?(getFloatVector)).nil?
	throw "getFloatVector already defined!"
getFloatVector = putFloatVector
getFloatVectorType = typeof getFloatVector[0]
print(getFloatVectorType)
print(getFloatVector)
if(getFloatVectorType !== 'number')
	throw "getFloatVector not number!"

print("getFloatVectorAsList")
if (defined?(getFloatVectorAsList)).nil?
	throw "getFloatVectorAsList already defined!"
getFloatVectorAsList = putFloatVectorAsList
getFloatVectorAsListType = typeof getFloatVectorAsList[0]
print(getFloatVectorAsListType)
print(getFloatVectorAsList)
if(getFloatVectorAsListType !== 'number')
	throw "getFloatVectorAsList not number!"

print("getFloatMatrix")
if (defined?(getFloatMatrix)).nil?
	throw "getFloatMatrix already defined!"
getFloatMatrix = putFloatMatrix
getFloatMatrixType = typeof getFloatMatrix[0][0]
print(getFloatMatrixType)
print(getFloatMatrix)
if(getFloatMatrixType !== 'number')
	throw "getFloatMatrix not number!"

print("getFloatMatrixAsList")
if (defined?(getFloatMatrixAsList)).nil?
	throw "getFloatMatrixAsList already defined!"
getFloatMatrixAsList = putFloatMatrixAsList
getFloatMatrixAsListType = typeof getFloatMatrixAsList[0][0]
print(getFloatMatrixAsListType)
print(getFloatMatrixAsList)
if(getFloatMatrixAsListType !== 'number')
	throw "getFloatMatrixAsList not number!"

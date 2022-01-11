print("getFloat")
if(typeof getFloat !== 'undefined')
	throw "getFloat already defined!"
getFloat = putFloat
getFloatType = typeof getFloat
print(getFloatType)
print(getFloat)
if(getFloatType !== 'number')
	throw "getFloat not number!"

print("getFloatVector")
if(typeof getFloatVector !== 'undefined')
	throw "getFloatVector already defined!"
getFloatVector = putFloatVector
getFloatVectorType = typeof getFloatVector[0]
print(getFloatVectorType)
print(getFloatVector)
if(getFloatVectorType !== 'number')
	throw "getFloatVector not number!"

print("getFloatVectorAsList")
if(typeof getFloatVectorAsList !== 'undefined')
	throw "getFloatVectorAsList already defined!"
getFloatVectorAsList = putFloatVectorAsList
getFloatVectorAsListType = typeof getFloatVectorAsList[0]
print(getFloatVectorAsListType)
print(getFloatVectorAsList)
if(getFloatVectorAsListType !== 'number')
	throw "getFloatVectorAsList not number!"

print("getFloatMatrix")
if(typeof getFloatMatrix !== 'undefined')
	throw "getFloatMatrix already defined!"
getFloatMatrix = putFloatMatrix
getFloatMatrixType = typeof getFloatMatrix[0][0]
print(getFloatMatrixType)
print(getFloatMatrix)
if(getFloatMatrixType !== 'number')
	throw "getFloatMatrix not number!"

print("getFloatMatrixAsList")
if(typeof getFloatMatrixAsList !== 'undefined')
	throw "getFloatMatrixAsList already defined!"
getFloatMatrixAsList = putFloatMatrixAsList
getFloatMatrixAsListType = typeof getFloatMatrixAsList[0][0]
print(getFloatMatrixAsListType)
print(getFloatMatrixAsList)
if(getFloatMatrixAsListType !== 'number')
	throw "getFloatMatrixAsList not number!"

print("getFloat")
if(typeof getFloat !== 'undefined')
	throw "getFloat already defined!"
getFloat = callback("getFloat")
getFloatType = typeof getFloat
print(getFloatType)
print(getFloat)
if(getFloatType !== 'number')
	throw "getFloat not number!"
callback("setFloat",getFloat)

print("getFloatVector")
if(typeof getFloatVector !== 'undefined')
	throw "getFloatVector already defined!"
getFloatVector = callback("getFloatVector")
getFloatVectorType = typeof getFloatVector[0]
print(getFloatVectorType)
print(getFloatVector)
if(getFloatVectorType !== 'number')
	throw "getFloatVector not number!"
callback("setFloatVector",getFloatVector)

print("getFloatVectorAsList")
if(typeof getFloatVectorAsList !== 'undefined')
	throw "getFloatVectorAsList already defined!"
getFloatVectorAsList = callback("getFloatVectorAsList")
getFloatVectorAsListType = typeof getFloatVectorAsList[0]
print(getFloatVectorAsListType)
print(getFloatVectorAsList)
if(getFloatVectorAsListType !== 'number')
	throw "getFloatVectorAsList not number!"
callback("setFloatVectorAsList",getFloatVectorAsList)

print("getFloatMatrix")
if(typeof getFloatMatrix !== 'undefined')
	throw "getFloatMatrix already defined!"
getFloatMatrix = callback("getFloatMatrix")
getFloatMatrixType = typeof getFloatMatrix[0][0]
print(getFloatMatrixType)
print(getFloatMatrix)
if(getFloatMatrixType !== 'number')
	throw "getFloatMatrix not number!"
callback("setFloatMatrix",getFloatMatrix)

print("getFloatMatrixAsList")
if(typeof getFloatMatrixAsList !== 'undefined')
	throw "getFloatMatrixAsList already defined!"
getFloatMatrixAsList = callback("getFloatMatrixAsList")
getFloatMatrixAsListType = typeof getFloatMatrixAsList[0][0]
print(getFloatMatrixAsListType)
print(getFloatMatrixAsList)
if(getFloatMatrixAsListType !== 'number')
	throw "getFloatMatrixAsList not number!"
callback("setFloatMatrixAsList",getFloatMatrixAsList)

print("getBoolean")
if(typeof getBoolean !== 'undefined')
	throw "getBoolean already defined!"
getBoolean = putBoolean
getBooleanType = typeof getBoolean
print(getBooleanType)
print(getBoolean)
if(getBooleanType !== 'boolean')
	throw "getBoolean not boolean!"

print("getBooleanVector")
if(typeof getBooleanVector !== 'undefined')
	throw "getBooleanVector already defined!"
getBooleanVector = putBooleanVector
getBooleanVectorType = typeof getBooleanVector[0]
print(getBooleanVectorType)
print(getBooleanVector)
if(getBooleanVectorType !== 'boolean')
	throw "getBooleanVector not boolean!"

print("getBooleanVectorAsList")
if(typeof getBooleanVectorAsList !== 'undefined')
	throw "getBooleanVectorAsList already defined!"
getBooleanVectorAsList = putBooleanVectorAsList
getBooleanVectorAsListType = typeof getBooleanVectorAsList[0]
print(getBooleanVectorAsListType)
print(getBooleanVectorAsList)
if(getBooleanVectorAsListType !== 'boolean')
	throw "getBooleanVectorAsList not boolean!"

print("getBooleanMatrix")
if(typeof getBooleanMatrix !== 'undefined')
	throw "getBooleanMatrix already defined!"
getBooleanMatrix = putBooleanMatrix
getBooleanMatrixType = typeof getBooleanMatrix[0][0]
print(getBooleanMatrixType)
print(getBooleanMatrix)
if(getBooleanMatrixType !== 'boolean')
	throw "getBooleanMatrix not boolean!"

print("getBooleanMatrixAsList")
if(typeof getBooleanMatrixAsList !== 'undefined')
	throw "getBooleanMatrixAsList already defined!"
getBooleanMatrixAsList = putBooleanMatrixAsList
getBooleanMatrixAsListType = typeof getBooleanMatrixAsList[0][0]
print(getBooleanMatrixAsListType)
print(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType !== 'boolean')
	throw "getBooleanMatrixAsList not boolean!"

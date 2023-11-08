print("getBoolean")
if(typeof getBoolean !== 'undefined')
	throw "getBoolean already defined!"
getBoolean = callback("getBoolean")
getBooleanType = typeof getBoolean
print(getBooleanType)
print(getBoolean)
if(getBooleanType !== 'boolean')
	throw "getBoolean not boolean!"
callback("setBoolean",getBoolean)

print("getBooleanVector")
if(typeof getBooleanVector !== 'undefined')
	throw "getBooleanVector already defined!"
getBooleanVector = callback("getBooleanVector")
getBooleanVectorType = typeof getBooleanVector[0]
print(getBooleanVectorType)
print(getBooleanVector)
if(getBooleanVectorType !== 'boolean')
	throw "getBooleanVector not boolean!"
callback("setBooleanVector",getBooleanVector)

print("getBooleanVectorAsList")
if(typeof getBooleanVectorAsList !== 'undefined')
	throw "getBooleanVectorAsList already defined!"
getBooleanVectorAsList = callback("getBooleanVectorAsList")
getBooleanVectorAsListType = typeof getBooleanVectorAsList[0]
print(getBooleanVectorAsListType)
print(getBooleanVectorAsList)
if(getBooleanVectorAsListType !== 'boolean')
	throw "getBooleanVectorAsList not boolean!"
callback("setBooleanVectorAsList",getBooleanVectorAsList)

print("getBooleanMatrix")
if(typeof getBooleanMatrix !== 'undefined')
	throw "getBooleanMatrix already defined!"
getBooleanMatrix = callback("getBooleanMatrix")
getBooleanMatrixType = typeof getBooleanMatrix[0][0]
print(getBooleanMatrixType)
print(getBooleanMatrix)
if(getBooleanMatrixType !== 'boolean')
	throw "getBooleanMatrix not boolean!"
callback("setBooleanMatrix",getBooleanMatrix)

print("getBooleanMatrixAsList")
if(typeof getBooleanMatrixAsList !== 'undefined')
	throw "getBooleanMatrixAsList already defined!"
getBooleanMatrixAsList = callback("getBooleanMatrixAsList")
getBooleanMatrixAsListType = typeof getBooleanMatrixAsList[0][0]
print(getBooleanMatrixAsListType)
print(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType !== 'boolean')
	throw "getBooleanMatrixAsList not boolean!"
callback("setBooleanMatrixAsList",getBooleanMatrixAsList)

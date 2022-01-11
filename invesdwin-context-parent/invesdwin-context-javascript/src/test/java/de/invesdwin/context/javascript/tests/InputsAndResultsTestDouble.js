print("getDouble")
if(typeof getDouble !== 'undefined')
	throw "getDouble already defined!"
getDouble = putDouble
getDoubleType = typeof getDouble
print(getDoubleType)
print(getDouble)
if(getDoubleType !== 'number')
	throw "getDouble not number!"

print("getDoubleVector")
if(typeof getDoubleVector !== 'undefined')
	throw "getDoubleVector already defined!"
getDoubleVector = putDoubleVector
getDoubleVectorType = typeof getDoubleVector[0]
print(getDoubleVectorType)
print(getDoubleVector)
if(getDoubleVectorType !== 'number')
	throw "getDoubleVector not number!"

print("getDoubleVectorAsList")
if(typeof getDoubleVectorAsList !== 'undefined')
	throw "getDoubleVectorAsList already defined!"
getDoubleVectorAsList = putDoubleVectorAsList
getDoubleVectorAsListType = typeof getDoubleVectorAsList[0]
print(getDoubleVectorAsListType)
print(getDoubleVectorAsList)
if(getDoubleVectorAsListType !== 'number')
	throw "getDoubleVectorAsList not number!"

print("getDoubleMatrix")
if(typeof getDoubleMatrix !== 'undefined')
	throw "getDoubleMatrix already defined!"
getDoubleMatrix = putDoubleMatrix
getDoubleMatrixType = typeof getDoubleMatrix[0][0]
print(getDoubleMatrixType)
print(getDoubleMatrix)
if(getDoubleMatrixType !== 'number')
	throw "getDoubleMatrix not number!"

print("getDoubleMatrixAsList")
if(typeof getDoubleMatrixAsList !== 'undefined')
	throw "getDoubleMatrixAsList already defined!"
getDoubleMatrixAsList = putDoubleMatrixAsList
getDoubleMatrixAsListType = typeof getDoubleMatrixAsList[0][0]
print(getDoubleMatrixAsListType)
print(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType !== 'number')
	throw "getDoubleMatrixAsList not number!"

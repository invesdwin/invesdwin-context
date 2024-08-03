print("getDouble")
if(typeof getDouble !== 'undefined')
	throw "getDouble already defined!"
getDouble = putDouble
getDoubleType = typeof getDouble
print(getDoubleType)
print(getDouble)
if(getDoubleType !== 'number')
	throw "getDouble not number!"
if(!isNaN(getDouble))
	throw "getDouble not NaN!"

print("getDoubleVector")
if(typeof getDoubleVector !== 'undefined')
	throw "getDoubleVector already defined!"
getDoubleVector = putDoubleVector
getDoubleVectorType = typeof getDoubleVector[0]
print(getDoubleVectorType)
print(getDoubleVector)
if(getDoubleVectorType !== 'number')
	throw "getDoubleVector not number!"
if(!isNaN(getDoubleVector[1]))
	throw "getDoubleVector[1] not NaN!"

print("getDoubleVectorAsList")
if(typeof getDoubleVectorAsList !== 'undefined')
	throw "getDoubleVectorAsList already defined!"
getDoubleVectorAsList = putDoubleVectorAsList
getDoubleVectorAsListType = typeof getDoubleVectorAsList[0]
print(getDoubleVectorAsListType)
print(getDoubleVectorAsList)
if(getDoubleVectorAsListType !== 'number')
	throw "getDoubleVectorAsList not number!"
if(!isNaN(getDoubleVectorAsList[1]))
	throw "getDoubleVectorAsList[1] not NaN!"

print("getDoubleMatrix")
if(typeof getDoubleMatrix !== 'undefined')
	throw "getDoubleMatrix already defined!"
getDoubleMatrix = putDoubleMatrix
getDoubleMatrixType = typeof getDoubleMatrix[0][0]
print(getDoubleMatrixType)
print(getDoubleMatrix)
if(getDoubleMatrixType !== 'number')
	throw "getDoubleMatrix not number!"
if(!isNaN(getDoubleMatrix[0][0]))
	throw "getDoubleMatrix[0][0] not NaN!"
if(!isNaN(getDoubleMatrix[1][1]))
	throw "getDoubleMatrix[1][1] not NaN!"
if(!isNaN(getDoubleMatrix[2][2]))
	throw "getDoubleMatrix[2][2] not NaN!"

print("getDoubleMatrixAsList")
if(typeof getDoubleMatrixAsList !== 'undefined')
	throw "getDoubleMatrixAsList already defined!"
getDoubleMatrixAsList = putDoubleMatrixAsList
getDoubleMatrixAsListType = typeof getDoubleMatrixAsList[0][0]
print(getDoubleMatrixAsListType)
print(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType !== 'number')
	throw "getDoubleMatrixAsList not number!"
if(!isNaN(getDoubleMatrixAsList[0][0]))
	throw "getDoubleMatrixAsList[0][0] not NaN!"
if(!isNaN(getDoubleMatrixAsList[1][1]))
	throw "getDoubleMatrixAsList[1][1] not NaN!"
if(!isNaN(getDoubleMatrixAsList[2][2]))
	throw "getDoubleMatrixAsList[2][2] not NaN!"

print("getDouble")
if(typeof getDouble !== 'undefined')
	throw "getDouble already defined!"
getDouble = callback("getDouble")
getDoubleType = typeof getDouble
print(getDoubleType)
print(getDouble)
if(getDoubleType !== 'number')
	throw "getDouble not number!"
if(!isNaN(getDouble))
	throw "getDouble not NaN!"
callback("setDouble",getDouble)

print("getDoubleVector")
if(typeof getDoubleVector !== 'undefined')
	throw "getDoubleVector already defined!"
getDoubleVector = callback("getDoubleVector")
getDoubleVectorType = typeof getDoubleVector[0]
print(getDoubleVectorType)
print(getDoubleVector)
if(getDoubleVectorType !== 'number')
	throw "getDoubleVector not number!"
if(!isNaN(getDoubleVector[1]))
	throw "getDoubleVector[1] not NaN!"
callback("setDoubleVector",getDoubleVector)

print("getDoubleVectorAsList")
if(typeof getDoubleVectorAsList !== 'undefined')
	throw "getDoubleVectorAsList already defined!"
getDoubleVectorAsList = callback("getDoubleVectorAsList")
getDoubleVectorAsListType = typeof getDoubleVectorAsList[0]
print(getDoubleVectorAsListType)
print(getDoubleVectorAsList)
if(getDoubleVectorAsListType !== 'number')
	throw "getDoubleVectorAsList not number!"
if(!isNaN(getDoubleVectorAsList[1]))
	throw "getDoubleVectorAsList[1] not NaN!"
callback("setDoubleVectorAsList",getDoubleVectorAsList)

print("getDoubleMatrix")
if(typeof getDoubleMatrix !== 'undefined')
	throw "getDoubleMatrix already defined!"
getDoubleMatrix = callback("getDoubleMatrix")
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
callback("setDoubleMatrix",getDoubleMatrix)

print("getDoubleMatrixAsList")
if(typeof getDoubleMatrixAsList !== 'undefined')
	throw "getDoubleMatrixAsList already defined!"
getDoubleMatrixAsList = callback("getDoubleMatrixAsList")
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
callback("setDoubleMatrixAsList",getDoubleMatrixAsList)

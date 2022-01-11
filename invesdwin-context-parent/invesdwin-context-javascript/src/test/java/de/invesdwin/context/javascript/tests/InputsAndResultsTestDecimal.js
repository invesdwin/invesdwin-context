print("getDecimal")
if(typeof getDecimal !== 'undefined')
	throw "getDecimal already defined!"
getDecimal = putDecimal
getDecimalType = typeof getDecimal
print(getDecimalType)
print(getDecimal)
if(getDecimalType !== 'number')
	throw "getDecimal not number!"

print("getDecimalVector")
if(typeof getDecimalVector !== 'undefined')
	throw "getDecimalVector already defined!"
getDecimalVector = putDecimalVector
getDecimalVectorType = typeof getDecimalVector[0]
print(getDecimalVectorType)
print(getDecimalVector)
if(getDecimalVectorType !== 'number')
	throw "getDecimalVector not number!"

print("getDecimalVectorAsList")
if(typeof getDecimalVectorAsList !== 'undefined')
	throw "getDecimalVectorAsList already defined!"
getDecimalVectorAsList = putDecimalVectorAsList
getDecimalVectorAsListType = typeof getDecimalVectorAsList[0]
print(getDecimalVectorAsListType)
print(getDecimalVectorAsList)
if(getDecimalVectorAsListType !== 'number')
	throw "getDecimalVectorAsList not number!"

print("getDecimalMatrix")
if(typeof getDecimalMatrix !== 'undefined')
	throw "getDecimalMatrix already defined!"
getDecimalMatrix = putDecimalMatrix
getDecimalMatrixType = typeof getDecimalMatrix[0][0]
print(getDecimalMatrixType)
print(getDecimalMatrix)
if(getDecimalMatrixType !== 'number')
	throw "getDecimalMatrix not number!"

print("getDecimalMatrixAsList")
if(typeof getDecimalMatrixAsList !== 'undefined')
	throw "getDecimalMatrixAsList already defined!"
getDecimalMatrixAsList = putDecimalMatrixAsList
getDecimalMatrixAsListType = typeof getDecimalMatrixAsList[0][0]
print(getDecimalMatrixAsListType)
print(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType !== 'number')
	throw "getDecimalMatrixAsList not number!"

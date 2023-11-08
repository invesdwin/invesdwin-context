print("getDecimal")
if(typeof getDecimal !== 'undefined')
	throw "getDecimal already defined!"
getDecimal = callback("getDecimal")
getDecimalType = typeof getDecimal
print(getDecimalType)
print(getDecimal)
if(getDecimalType !== 'number')
	throw "getDecimal not number!"
callback("setDecimal",getDecimal)

print("getDecimalVector")
if(typeof getDecimalVector !== 'undefined')
	throw "getDecimalVector already defined!"
getDecimalVector = callback("getDecimalVector")
getDecimalVectorType = typeof getDecimalVector[0]
print(getDecimalVectorType)
print(getDecimalVector)
if(getDecimalVectorType !== 'number')
	throw "getDecimalVector not number!"
callback("setDecimalVector",getDecimalVector)

print("getDecimalVectorAsList")
if(typeof getDecimalVectorAsList !== 'undefined')
	throw "getDecimalVectorAsList already defined!"
getDecimalVectorAsList = callback("getDecimalVectorAsList")
getDecimalVectorAsListType = typeof getDecimalVectorAsList[0]
print(getDecimalVectorAsListType)
print(getDecimalVectorAsList)
if(getDecimalVectorAsListType !== 'number')
	throw "getDecimalVectorAsList not number!"
callback("setDecimalVectorAsList",getDecimalVectorAsList)

print("getDecimalMatrix")
if(typeof getDecimalMatrix !== 'undefined')
	throw "getDecimalMatrix already defined!"
getDecimalMatrix = callback("getDecimalMatrix")
getDecimalMatrixType = typeof getDecimalMatrix[0][0]
print(getDecimalMatrixType)
print(getDecimalMatrix)
if(getDecimalMatrixType !== 'number')
	throw "getDecimalMatrix not number!"
callback("setDecimalMatrix",getDecimalMatrix)

print("getDecimalMatrixAsList")
if(typeof getDecimalMatrixAsList !== 'undefined')
	throw "getDecimalMatrixAsList already defined!"
getDecimalMatrixAsList = callback("getDecimalMatrixAsList")
getDecimalMatrixAsListType = typeof getDecimalMatrixAsList[0][0]
print(getDecimalMatrixAsListType)
print(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType !== 'number')
	throw "getDecimalMatrixAsList not number!"
callback("setDecimalMatrixAsList",getDecimalMatrixAsList)

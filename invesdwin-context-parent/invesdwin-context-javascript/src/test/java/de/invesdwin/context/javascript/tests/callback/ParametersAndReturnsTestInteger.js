print("getInteger")
if(typeof getInteger !== 'undefined')
	throw "getInteger already defined!"
getInteger = callback("getInteger")
getIntegerType = typeof getInteger
print(getIntegerType)
print(getInteger)
if(getIntegerType !== 'number')
	throw "getInteger not number!"
callback("setInteger",getInteger)

print("getIntegerVector")
if(typeof getIntegerVector !== 'undefined')
	throw "getIntegerVector already defined!"
getIntegerVector = callback("getIntegerVector")
getIntegerVectorType = typeof getIntegerVector[0]
print(getIntegerVectorType)
print(getIntegerVector)
if(getIntegerVectorType !== 'number')
	throw "getIntegerVector not number!"
callback("setIntegerVector",getIntegerVector)

print("getIntegerVectorAsList")
if(typeof getIntegerVectorAsList !== 'undefined')
	throw "getIntegerVectorAsList already defined!"
getIntegerVectorAsList = callback("getIntegerVectorAsList")
getIntegerVectorAsListType = typeof getIntegerVectorAsList[0]
print(getIntegerVectorAsListType)
print(getIntegerVectorAsList)
if(getIntegerVectorAsListType !== 'number')
	throw "getIntegerVectorAsList not number!"
callback("setIntegerVectorAsList",getIntegerVectorAsList)

print("getIntegerMatrix")
if(typeof getIntegerMatrix !== 'undefined')
	throw "getIntegerMatrix already defined!"
getIntegerMatrix = callback("getIntegerMatrix")
getIntegerMatrixType = typeof getIntegerMatrix[0][0]
print(getIntegerMatrixType)
print(getIntegerMatrix)
if(getIntegerMatrixType !== 'number')
	throw "getIntegerMatrix not number!"
callback("setIntegerMatrix",getIntegerMatrix)

print("getIntegerMatrixAsList")
if(typeof getIntegerMatrixAsList !== 'undefined')
	throw "getIntegerMatrixAsList already defined!"
getIntegerMatrixAsList = callback("getIntegerMatrixAsList")
getIntegerMatrixAsListType = typeof getIntegerMatrixAsList[0][0]
print(getIntegerMatrixAsListType)
print(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType !== 'number')
	throw "getIntegerMatrixAsList not number!"
callback("setIntegerMatrixAsList",getIntegerMatrixAsList)

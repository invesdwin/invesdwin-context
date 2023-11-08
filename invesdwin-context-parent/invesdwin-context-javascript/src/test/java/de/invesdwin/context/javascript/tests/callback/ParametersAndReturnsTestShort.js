print("getShort")
if(typeof getShort !== 'undefined')
	throw "getShort already defined!"
getShort = callback("getShort")
getShortType = typeof getShort
print(getShortType)
print(getShort)
if(getShortType !== 'number')
	throw "getShort not number!"
callback("setShort",getShort)

print("getShortVector")
if(typeof getShortVector !== 'undefined')
	throw "getShortVector already defined!"
getShortVector = callback("getShortVector")
getShortVectorType = typeof getShortVector[0]
print(getShortVectorType)
print(getShortVector)
if(getShortVectorType !== 'number')
	throw "getShortVector not number!"
callback("setShortVector",getShortVector)

print("getShortVectorAsList")
if(typeof getShortVectorAsList !== 'undefined')
	throw "getShortVectorAsList already defined!"
getShortVectorAsList = callback("getShortVectorAsList")
getShortVectorAsListType = typeof getShortVectorAsList[0]
print(getShortVectorAsListType)
print(getShortVectorAsList)
if(getShortVectorAsListType !== 'number')
	throw "getShortVectorAsList not number!"
callback("setShortVectorAsList",getShortVectorAsList)

print("getShortMatrix")
if(typeof getShortMatrix !== 'undefined')
	throw "getShortMatrix already defined!"
getShortMatrix = callback("getShortMatrix")
getShortMatrixType = typeof getShortMatrix[0][0]
print(getShortMatrixType)
print(getShortMatrix)
if(getShortMatrixType !== 'number')
	throw "getShortMatrix not number!"
callback("setShortMatrix",getShortMatrix)

print("getShortMatrixAsList")
if(typeof getShortMatrixAsList !== 'undefined')
	throw "getShortMatrixAsList already defined!"
getShortMatrixAsList = callback("getShortMatrixAsList")
getShortMatrixAsListType = typeof getShortMatrixAsList[0][0]
print(getShortMatrixAsListType)
print(getShortMatrixAsList)
if(getShortMatrixAsListType !== 'number')
	throw "getShortMatrixAsList not number!"
callback("setShortMatrixAsList",getShortMatrixAsList)

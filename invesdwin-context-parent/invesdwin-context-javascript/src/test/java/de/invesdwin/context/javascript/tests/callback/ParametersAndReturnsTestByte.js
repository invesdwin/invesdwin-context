print("getByte")
if(typeof getByte !== 'undefined')
	throw "getByte already defined"
getByte = callback("getByte")
getByteType = typeof getByte
print(getByteType)
print(getByte)
if(getByteType !== 'number')
	throw "getByte not number!"
callback("setByte",getByte)

print("getByteVector")
if(typeof getByteVector !== 'undefined')
	throw "getByteVector already defined"
getByteVector = callback("getByteVector")
getByteVectorType = typeof getByteVector[0]
print(getByteVectorType)
print(getByteVector)
if(getByteVectorType !== 'number')
	throw "getByteVector not number!"
callback("setByteVector",getByteVector)

print("getByteVectorAsList")
if(typeof getByteVectorAsList !== 'undefined')
	throw "getByteVectorAsList already defined"
getByteVectorAsList = callback("getByteVectorAsList")
getByteVectorAsListType = typeof getByteVectorAsList[0]
print(getByteVectorAsListType)
print(getByteVectorAsList)
if(getByteVectorAsListType !== 'number')
	throw "getByteVectorAsList not number!"
callback("setByteVectorAsList",getByteVectorAsList)

print("getByteMatrix")
if(typeof getByteMatrix !== 'undefined')
	throw "getByteMatrix already defined"
getByteMatrix = callback("getByteMatrix")
getByteMatrixType = typeof getByteMatrix[0][0]
print(getByteMatrixType)
print(getByteMatrix)
if(getByteMatrixType !== 'number')
	throw "getByteMatrix not number!"
callback("setByteMatrix",getByteMatrix)

print("getByteMatrixAsList")
if(typeof getByteMatrixAsList !== 'undefined')
	throw "getByteMatrixAsList already defined"
getByteMatrixAsList = callback("getByteMatrixAsList")
getByteMatrixAsListType = typeof getByteMatrixAsList[0][0]
print(getByteMatrixAsListType)
print(getByteMatrixAsList)
if(getByteMatrixAsListType !== 'number')
	throw "getByteMatrixAsList not number!"
callback("setByteMatrixAsList",getByteMatrixAsList)

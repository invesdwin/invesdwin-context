print("getByte")
if(typeof getByte !== 'undefined')
	throw "getByte already defined"
getByte = putByte
getByteType = typeof getByte
print(getByteType)
print(getByte)
if(getByteType !== 'number')
	throw "getByte not number!"

print("getByteVector")
if(typeof getByteVector !== 'undefined')
	throw "getByteVector already defined"
getByteVector = putByteVector
getByteVectorType = typeof getByteVector[0]
print(getByteVectorType)
print(getByteVector)
if(getByteVectorType !== 'number')
	throw "getByteVector not number!"

print("getByteVectorAsList")
if(typeof getByteVectorAsList !== 'undefined')
	throw "getByteVectorAsList already defined"
getByteVectorAsList = putByteVectorAsList
getByteVectorAsListType = typeof getByteVectorAsList[0]
print(getByteVectorAsListType)
print(getByteVectorAsList)
if(getByteVectorAsListType !== 'number')
	throw "getByteVectorAsList not number!"

print("getByteMatrix")
if(typeof getByteMatrix !== 'undefined')
	throw "getByteMatrix already defined"
getByteMatrix = putByteMatrix
getByteMatrixType = typeof getByteMatrix[0][0]
print(getByteMatrixType)
print(getByteMatrix)
if(getByteMatrixType !== 'number')
	throw "getByteMatrix not number!"

print("getByteMatrixAsList")
if(typeof getByteMatrixAsList !== 'undefined')
	throw "getByteMatrixAsList already defined"
getByteMatrixAsList = putByteMatrixAsList
getByteMatrixAsListType = typeof getByteMatrixAsList[0][0]
print(getByteMatrixAsListType)
print(getByteMatrixAsList)
if(getByteMatrixAsListType !== 'number')
	throw "getByteMatrixAsList not number!"

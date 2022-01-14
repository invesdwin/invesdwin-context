print("getByte")
if (defined?(getByte)).nil?
	throw "getByte already defined"
getByte = putByte
getByteType = typeof getByte
print(getByteType)
print(getByte)
if(getByteType !== 'number')
	throw "getByte not number!"

print("getByteVector")
if (defined?(getByteVector)).nil?
	throw "getByteVector already defined"
getByteVector = putByteVector
getByteVectorType = typeof getByteVector[0]
print(getByteVectorType)
print(getByteVector)
if(getByteVectorType !== 'number')
	throw "getByteVector not number!"

print("getByteVectorAsList")
if (defined?(getByteVectorAsList)).nil?
	throw "getByteVectorAsList already defined"
getByteVectorAsList = putByteVectorAsList
getByteVectorAsListType = typeof getByteVectorAsList[0]
print(getByteVectorAsListType)
print(getByteVectorAsList)
if(getByteVectorAsListType !== 'number')
	throw "getByteVectorAsList not number!"

print("getByteMatrix")
if (defined?(getByteMatrix)).nil?
	throw "getByteMatrix already defined"
getByteMatrix = putByteMatrix
getByteMatrixType = typeof getByteMatrix[0][0]
print(getByteMatrixType)
print(getByteMatrix)
if(getByteMatrixType !== 'number')
	throw "getByteMatrix not number!"

print("getByteMatrixAsList")
if (defined?(getByteMatrixAsList)).nil?
	throw "getByteMatrixAsList already defined"
getByteMatrixAsList = putByteMatrixAsList
getByteMatrixAsListType = typeof getByteMatrixAsList[0][0]
print(getByteMatrixAsListType)
print(getByteMatrixAsList)
if(getByteMatrixAsListType !== 'number')
	throw "getByteMatrixAsList not number!"

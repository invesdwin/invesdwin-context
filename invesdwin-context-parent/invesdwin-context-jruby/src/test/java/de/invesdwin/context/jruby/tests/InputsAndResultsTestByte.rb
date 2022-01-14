print("getByte")
if (defined?(getByte)).nil?
	raise "getByte already defined"
getByte = putByte
getByteType = getByte.class
print(getByteType)
print(getByte)
if(getByteType != 'number')
	raise "getByte not number!"

print("getByteVector")
if (defined?(getByteVector)).nil?
	raise "getByteVector already defined"
getByteVector = putByteVector
getByteVectorType = getByteVector[0].class
print(getByteVectorType)
print(getByteVector)
if(getByteVectorType != 'number')
	raise "getByteVector not number!"

print("getByteVectorAsList")
if (defined?(getByteVectorAsList)).nil?
	raise "getByteVectorAsList already defined"
getByteVectorAsList = putByteVectorAsList
getByteVectorAsListType = getByteVectorAsList[0].class
print(getByteVectorAsListType)
print(getByteVectorAsList)
if(getByteVectorAsListType != 'number')
	raise "getByteVectorAsList not number!"

print("getByteMatrix")
if (defined?(getByteMatrix)).nil?
	raise "getByteMatrix already defined"
getByteMatrix = putByteMatrix
getByteMatrixType = getByteMatrix[0][0].class
print(getByteMatrixType)
print(getByteMatrix)
if(getByteMatrixType != 'number')
	raise "getByteMatrix not number!"

print("getByteMatrixAsList")
if (defined?(getByteMatrixAsList)).nil?
	raise "getByteMatrixAsList already defined"
getByteMatrixAsList = putByteMatrixAsList
getByteMatrixAsListType = getByteMatrixAsList[0][0].class
print(getByteMatrixAsListType)
print(getByteMatrixAsList)
if(getByteMatrixAsListType != 'number')
	raise "getByteMatrixAsList not number!"

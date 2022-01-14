print("getBoolean")
if (defined?(getBoolean)).nil?
	raise "getBoolean already defined!"
getBoolean = putBoolean
getBooleanType = getBoolean.class
print(getBooleanType)
print(getBoolean)
if(getBooleanType != 'boolean')
	raise "getBoolean not boolean!"

print("getBooleanVector")
if (defined?(getBooleanVector)).nil?
	raise "getBooleanVector already defined!"
getBooleanVector = putBooleanVector
getBooleanVectorType = getBooleanVector[0].class
print(getBooleanVectorType)
print(getBooleanVector)
if(getBooleanVectorType != 'boolean')
	raise "getBooleanVector not boolean!"

print("getBooleanVectorAsList")
if (defined?(getBooleanVectorAsList)).nil?
	raise "getBooleanVectorAsList already defined!"
getBooleanVectorAsList = putBooleanVectorAsList
getBooleanVectorAsListType = getBooleanVectorAsList[0].class
print(getBooleanVectorAsListType)
print(getBooleanVectorAsList)
if(getBooleanVectorAsListType != 'boolean')
	raise "getBooleanVectorAsList not boolean!"

print("getBooleanMatrix")
if (defined?(getBooleanMatrix)).nil?
	raise "getBooleanMatrix already defined!"
getBooleanMatrix = putBooleanMatrix
getBooleanMatrixType = getBooleanMatrix[0][0].class
print(getBooleanMatrixType)
print(getBooleanMatrix)
if(getBooleanMatrixType != 'boolean')
	raise "getBooleanMatrix not boolean!"

print("getBooleanMatrixAsList")
if (defined?(getBooleanMatrixAsList)).nil?
	raise "getBooleanMatrixAsList already defined!"
getBooleanMatrixAsList = putBooleanMatrixAsList
getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].class
print(getBooleanMatrixAsListType)
print(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != 'boolean')
	raise "getBooleanMatrixAsList not boolean!"

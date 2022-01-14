print("getLong")
if (defined?(getLong)).nil?
	raise "getLong already defined!"
getLong = putLong
getLongType = getLong.class
print(getLongType)
print(getLong)
if(getLongType != 'number')
	raise "getLong not number!"

print("getLongVector")
if (defined?(getLongVector)).nil?
	raise "getLongVector already defined!"
getLongVector = putLongVector
getLongVectorType = getLongVector[0].class
print(getLongVectorType)
print(getLongVector)
if(getLongVectorType != 'number')
	raise "getLongVector not number!"

print("getLongVectorAsList")
if (defined?(getLongVectorAsList)).nil?
	raise "getLongVectorAsList already defined!"
getLongVectorAsList = putLongVectorAsList
getLongVectorAsListType = getLongVectorAsList[0].class
print(getLongVectorAsListType)
print(getLongVectorAsList)
if(getLongVectorAsListType != 'number')
	raise "getLongVectorAsList not number!"

print("getLongMatrix")
if (defined?(getLongMatrix)).nil?
	raise "getLongMatrix already defined!"
getLongMatrix = putLongMatrix
getLongMatrixType = getLongMatrix[0][0].class
print(getLongMatrixType)
print(getLongMatrix)
if(getLongMatrixType != 'number')
	raise "getLongMatrix not number!"

print("getLongMatrixAsList")
if (defined?(getLongMatrixAsList)).nil?
	raise "getLongMatrixAsList already defined!"
getLongMatrixAsList = putLongMatrixAsList
getLongMatrixAsListType = getLongMatrixAsList[0][0].class
print(getLongMatrixAsListType)
print(getLongMatrixAsList)
if(getLongMatrixAsListType != 'number')
	raise "getLongMatrixAsList not number!"

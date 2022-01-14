print("getLong")
if (defined?(getLong)).nil?
	throw "getLong already defined!"
getLong = putLong
getLongType = typeof getLong
print(getLongType)
print(getLong)
if(getLongType !== 'number')
	throw "getLong not number!"

print("getLongVector")
if (defined?(getLongVector)).nil?
	throw "getLongVector already defined!"
getLongVector = putLongVector
getLongVectorType = typeof getLongVector[0]
print(getLongVectorType)
print(getLongVector)
if(getLongVectorType !== 'number')
	throw "getLongVector not number!"

print("getLongVectorAsList")
if (defined?(getLongVectorAsList)).nil?
	throw "getLongVectorAsList already defined!"
getLongVectorAsList = putLongVectorAsList
getLongVectorAsListType = typeof getLongVectorAsList[0]
print(getLongVectorAsListType)
print(getLongVectorAsList)
if(getLongVectorAsListType !== 'number')
	throw "getLongVectorAsList not number!"

print("getLongMatrix")
if (defined?(getLongMatrix)).nil?
	throw "getLongMatrix already defined!"
getLongMatrix = putLongMatrix
getLongMatrixType = typeof getLongMatrix[0][0]
print(getLongMatrixType)
print(getLongMatrix)
if(getLongMatrixType !== 'number')
	throw "getLongMatrix not number!"

print("getLongMatrixAsList")
if (defined?(getLongMatrixAsList)).nil?
	throw "getLongMatrixAsList already defined!"
getLongMatrixAsList = putLongMatrixAsList
getLongMatrixAsListType = typeof getLongMatrixAsList[0][0]
print(getLongMatrixAsListType)
print(getLongMatrixAsList)
if(getLongMatrixAsListType !== 'number')
	throw "getLongMatrixAsList not number!"

print("getLong")
if(typeof getLong !== 'undefined')
	throw "getLong already defined!"
getLong = callback("getLong")
getLongType = typeof getLong
print(getLongType)
print(getLong)
if(getLongType !== 'number')
	throw "getLong not number!"
callback("setLong",getLong)

print("getLongVector")
if(typeof getLongVector !== 'undefined')
	throw "getLongVector already defined!"
getLongVector = callback("getLongVector")
getLongVectorType = typeof getLongVector[0]
print(getLongVectorType)
print(getLongVector)
if(getLongVectorType !== 'number')
	throw "getLongVector not number!"
callback("setLongVector",getLongVector)

print("getLongVectorAsList")
if(typeof getLongVectorAsList !== 'undefined')
	throw "getLongVectorAsList already defined!"
getLongVectorAsList = callback("getLongVectorAsList")
getLongVectorAsListType = typeof getLongVectorAsList[0]
print(getLongVectorAsListType)
print(getLongVectorAsList)
if(getLongVectorAsListType !== 'number')
	throw "getLongVectorAsList not number!"
callback("setLongVectorAsList",getLongVectorAsList)

print("getLongMatrix")
if(typeof getLongMatrix !== 'undefined')
	throw "getLongMatrix already defined!"
getLongMatrix = callback("getLongMatrix")
getLongMatrixType = typeof getLongMatrix[0][0]
print(getLongMatrixType)
print(getLongMatrix)
if(getLongMatrixType !== 'number')
	throw "getLongMatrix not number!"
callback("setLongMatrix",getLongMatrix)

print("getLongMatrixAsList")
if(typeof getLongMatrixAsList !== 'undefined')
	throw "getLongMatrixAsList already defined!"
getLongMatrixAsList = callback("getLongMatrixAsList")
getLongMatrixAsListType = typeof getLongMatrixAsList[0][0]
print(getLongMatrixAsListType)
print(getLongMatrixAsList)
if(getLongMatrixAsListType !== 'number')
	throw "getLongMatrixAsList not number!"
callback("setLongMatrixAsList",getLongMatrixAsList)

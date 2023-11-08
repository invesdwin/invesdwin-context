print("getPercent")
if(typeof getPercent !== 'undefined')
	throw "getPercent already defined!"
getPercent = callback("getPercent")
getPercentType = typeof getPercent
print(getPercentType)
print(getPercent)
if(getPercentType !== 'number')
	throw "getPercent not number!"
callback("setPercent",getPercent)

print("getPercentVector")
if(typeof getPercentVector !== 'undefined')
	throw "getPercentVector already defined!"
getPercentVector = callback("getPercentVector")
getPercentVectorType = typeof getPercentVector[0]
print(getPercentVectorType)
print(getPercentVector)
if(getPercentVectorType !== 'number')
	throw "getPercentVector not number!"
callback("setPercentVector",getPercentVector)

print("getPercentVectorAsList")
if(typeof getPercentVectorAsList !== 'undefined')
	throw "getPercentVectorAsList already defined!"
getPercentVectorAsList = callback("getPercentVectorAsList")
getPercentVectorAsListType = typeof getPercentVectorAsList[0]
print(getPercentVectorAsListType)
print(getPercentVectorAsList)
if(getPercentVectorAsListType !== 'number')
	throw "getPercentVectorAsList not number!"
callback("setPercentVectorAsList",getPercentVectorAsList)

print("getPercentMatrix")
if(typeof getPercentMatrix !== 'undefined')
	throw "getPercentMatrix already defined!"
getPercentMatrix = callback("getPercentMatrix")
getPercentMatrixType = typeof getPercentMatrix[0][0]
print(getPercentMatrixType)
print(getPercentMatrix)
if(getPercentMatrixType !== 'number')
	throw "getPercentMatrix not number!"
callback("setPercentMatrix",getPercentMatrix)

print("getPercentMatrixAsList")
if(typeof getPercentMatrixAsList !== 'undefined')
	throw "getPercentMatrixAsList already defined!"
getPercentMatrixAsList = callback("getPercentMatrixAsList")
getPercentMatrixAsListType = typeof getPercentMatrixAsList[0][0]
print(getPercentMatrixAsListType)
print(getPercentMatrixAsList)
if(getPercentMatrixAsListType !== 'number')
	throw "getPercentMatrixAsList not number!"
callback("setPercentMatrixAsList",getPercentMatrixAsList)

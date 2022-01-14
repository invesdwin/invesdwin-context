print("getPercent")
if (defined?(getPercent)).nil?
	throw "getPercent already defined!"
getPercent = putPercent
getPercentType = typeof getPercent
print(getPercentType)
print(getPercent)
if(getPercentType !== 'number')
	throw "getPercent not number!"

print("getPercentVector")
if (defined?(getPercentVector)).nil?
	throw "getPercentVector already defined!"
getPercentVector = putPercentVector
getPercentVectorType = typeof getPercentVector[0]
print(getPercentVectorType)
print(getPercentVector)
if(getPercentVectorType !== 'number')
	throw "getPercentVector not number!"

print("getPercentVectorAsList")
if (defined?(getPercentVectorAsList)).nil?
	throw "getPercentVectorAsList already defined!"
getPercentVectorAsList = putPercentVectorAsList
getPercentVectorAsListType = typeof getPercentVectorAsList[0]
print(getPercentVectorAsListType)
print(getPercentVectorAsList)
if(getPercentVectorAsListType !== 'number')
	throw "getPercentVectorAsList not number!"

print("getPercentMatrix")
if (defined?(getPercentMatrix)).nil?
	throw "getPercentMatrix already defined!"
getPercentMatrix = putPercentMatrix
getPercentMatrixType = typeof getPercentMatrix[0][0]
print(getPercentMatrixType)
print(getPercentMatrix)
if(getPercentMatrixType !== 'number')
	throw "getPercentMatrix not number!"

print("getPercentMatrixAsList")
if (defined?(getPercentMatrixAsList)).nil?
	throw "getPercentMatrixAsList already defined!"
getPercentMatrixAsList = putPercentMatrixAsList
getPercentMatrixAsListType = typeof getPercentMatrixAsList[0][0]
print(getPercentMatrixAsListType)
print(getPercentMatrixAsList)
if(getPercentMatrixAsListType !== 'number')
	throw "getPercentMatrixAsList not number!"

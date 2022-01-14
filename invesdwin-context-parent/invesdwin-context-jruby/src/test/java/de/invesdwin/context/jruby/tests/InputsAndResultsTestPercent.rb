print("getPercent")
if (defined?(getPercent)).nil?
	raise "getPercent already defined!"
getPercent = putPercent
getPercentType = getPercent.class
print(getPercentType)
print(getPercent)
if(getPercentType != 'number')
	raise "getPercent not number!"

print("getPercentVector")
if (defined?(getPercentVector)).nil?
	raise "getPercentVector already defined!"
getPercentVector = putPercentVector
getPercentVectorType = getPercentVector[0].class
print(getPercentVectorType)
print(getPercentVector)
if(getPercentVectorType != 'number')
	raise "getPercentVector not number!"

print("getPercentVectorAsList")
if (defined?(getPercentVectorAsList)).nil?
	raise "getPercentVectorAsList already defined!"
getPercentVectorAsList = putPercentVectorAsList
getPercentVectorAsListType = getPercentVectorAsList[0].class
print(getPercentVectorAsListType)
print(getPercentVectorAsList)
if(getPercentVectorAsListType != 'number')
	raise "getPercentVectorAsList not number!"

print("getPercentMatrix")
if (defined?(getPercentMatrix)).nil?
	raise "getPercentMatrix already defined!"
getPercentMatrix = putPercentMatrix
getPercentMatrixType = getPercentMatrix[0][0].class
print(getPercentMatrixType)
print(getPercentMatrix)
if(getPercentMatrixType != 'number')
	raise "getPercentMatrix not number!"

print("getPercentMatrixAsList")
if (defined?(getPercentMatrixAsList)).nil?
	raise "getPercentMatrixAsList already defined!"
getPercentMatrixAsList = putPercentMatrixAsList
getPercentMatrixAsListType = getPercentMatrixAsList[0][0].class
print(getPercentMatrixAsListType)
print(getPercentMatrixAsList)
if(getPercentMatrixAsListType != 'number')
	raise "getPercentMatrixAsList not number!"

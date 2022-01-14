print("getString")
if (defined?(getString)).nil?
	throw "getString already defined!"
getString = putString
getStringType = typeof getString
print(getStringType)
print(getString)
if(getStringType !== 'string')
	throw "getString not string!"

print("getStringWithNull")
if (defined?(getStringWithNull)).nil?
	throw "getStringWithNull already defined!"
getStringWithNull = putStringWithNull
getStringWithNullType = typeof getStringWithNull
print(getStringWithNullType)
print(getStringWithNull)
if(getStringWithNull !== null)
	throw "getStringWithNull not null!"

print("getStringVector")
if (defined?(getStringVector)).nil?
	throw "getStringVector already defined!"
getStringVector = putStringVector
getStringVectorType = typeof getStringVector[0]
print(getStringVectorType)
print(getStringVector)
if(getStringVectorType !== 'string')
	throw "getStringVector not string!"

print("getStringVectorWithNull")
if (defined?(getStringVectorWithNull)).nil?
	throw "getStringVectorWithNull already defined!"
getStringVectorWithNull = putStringVectorWithNull
getStringVectorWithNullType = typeof getStringVectorWithNull[0]
print(getStringVectorWithNullType)
print(getStringVectorWithNull)
if(getStringVectorWithNullType !== 'string')
	throw "getStringVectorWithNull not string!"
if(getStringVectorWithNull[1] !== null)
	throw "getStringVectorWithNull[1] not null!"

print("getStringVectorAsList")
if (defined?(getStringVectorAsList)).nil?
	throw "getStringVectorAsList already defined!"
getStringVectorAsList = putStringVectorAsList
getStringVectorAsListType = typeof getStringVectorAsList[0]
print(getStringVectorAsListType)
print(getStringVectorAsList)
if(getStringVectorAsListType !== 'string')
	throw "getStringVectorAsList not string!"

print("getStringVectorAsListWithNull")
if (defined?(getStringVectorAsListWithNull)).nil?
	throw "getStringVectorAsListWithNull already defined!"
getStringVectorAsListWithNull = putStringVectorAsListWithNull
getStringVectorAsListWithNullType = typeof getStringVectorAsListWithNull[0]
print(getStringVectorAsListWithNullType)
print(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType !== 'string')
	throw "getStringVectorAsListWithNull not string!"
if(getStringVectorAsListWithNull[1] !== null)
	throw "getStringVectorAsListWithNull[1] not null!"

print("getStringMatrix")
if (defined?(getStringMatrix)).nil?
	throw "getStringMatrix already defined!"
getStringMatrix = putStringMatrix
getStringMatrixType = typeof getStringMatrix[0][0]
print(getStringMatrixType)
print(getStringMatrix)
if(getStringMatrixType !== 'string')
	throw "getStringMatrix not string!"

print("getStringMatrixWithNull")
if (defined?(getStringMatrixWithNull)).nil?
	throw "getStringMatrixWithNull already defined!"
getStringMatrixWithNull = putStringMatrixWithNull
getStringMatrixWithNullType = typeof getStringMatrixWithNull[0][1]
print(getStringMatrixWithNullType)
print(getStringMatrixWithNull)
if(getStringMatrixWithNullType !== 'string')
	throw "getStringMatrixWithNull not string!"
if(getStringMatrixWithNull[0][0] !== null)
	throw "getStringMatrixWithNull[0][0] not null!"
if(getStringMatrixWithNull[1][1] !== null)
	throw "getStringMatrixWithNull[1][1] not null!"
if(getStringMatrixWithNull[2][2] !== null)
	throw "getStringMatrixWithNull[2][2] not null!"

print("getStringMatrixAsList")
if (defined?(getStringMatrixAsList)).nil?
	throw "getStringMatrixAsList already defined!"
getStringMatrixAsList = putStringMatrixAsList
getStringMatrixAsListType = typeof getStringMatrixAsList[0][0]
print(getStringMatrixAsListType)
print(getStringMatrixAsList)
if(getStringMatrixAsListType !== 'string')
	throw "getStringMatrixAsList not string!"

print("getStringMatrixAsListWithNull")
if (defined?(getStringMatrixAsListWithNull)).nil?
	throw "getStringMatrixAsListWithNull already defined!"
getStringMatrixAsListWithNull = putStringMatrixAsListWithNull
getStringMatrixAsListWithNullType = typeof getStringMatrixAsListWithNull[0][1]
print(getStringMatrixAsListWithNullType)
print(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType !== 'string')
	throw "getStringMatrixAsListWithNull not string!"
if(getStringMatrixAsListWithNull[0][0] !== null)
	throw "getStringMatrixAsListWithNull[0][0] not null!"
if(getStringMatrixAsListWithNull[1][1] !== null)
	throw "getStringMatrixAsListWithNull[1][1] not null!"
if(getStringMatrixAsListWithNull[2][2] !== null)
	throw "getStringMatrixAsListWithNull[2][2] not null!"

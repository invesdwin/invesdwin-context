print("getString")
if (defined?(getString)).nil?
	raise "getString already defined!"
getString = putString
getStringType = getString.class
print(getStringType)
print(getString)
if(getStringType != 'string')
	raise "getString not string!"

print("getStringWithNull")
if (defined?(getStringWithNull)).nil?
	raise "getStringWithNull already defined!"
getStringWithNull = putStringWithNull
getStringWithNullType = getStringWithNull.class
print(getStringWithNullType)
print(getStringWithNull)
if(getStringWithNull != null)
	raise "getStringWithNull not null!"

print("getStringVector")
if (defined?(getStringVector)).nil?
	raise "getStringVector already defined!"
getStringVector = putStringVector
getStringVectorType = getStringVector[0].class
print(getStringVectorType)
print(getStringVector)
if(getStringVectorType != 'string')
	raise "getStringVector not string!"

print("getStringVectorWithNull")
if (defined?(getStringVectorWithNull)).nil?
	raise "getStringVectorWithNull already defined!"
getStringVectorWithNull = putStringVectorWithNull
getStringVectorWithNullType = getStringVectorWithNull[0].class
print(getStringVectorWithNullType)
print(getStringVectorWithNull)
if(getStringVectorWithNullType != 'string')
	raise "getStringVectorWithNull not string!"
if(getStringVectorWithNull[1] != null)
	raise "getStringVectorWithNull[1] not null!"

print("getStringVectorAsList")
if (defined?(getStringVectorAsList)).nil?
	raise "getStringVectorAsList already defined!"
getStringVectorAsList = putStringVectorAsList
getStringVectorAsListType = getStringVectorAsList[0].class
print(getStringVectorAsListType)
print(getStringVectorAsList)
if(getStringVectorAsListType != 'string')
	raise "getStringVectorAsList not string!"

print("getStringVectorAsListWithNull")
if (defined?(getStringVectorAsListWithNull)).nil?
	raise "getStringVectorAsListWithNull already defined!"
getStringVectorAsListWithNull = putStringVectorAsListWithNull
getStringVectorAsListWithNullType = getStringVectorAsListWithNull[0].class
print(getStringVectorAsListWithNullType)
print(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType != 'string')
	raise "getStringVectorAsListWithNull not string!"
if(getStringVectorAsListWithNull[1] != null)
	raise "getStringVectorAsListWithNull[1] not null!"

print("getStringMatrix")
if (defined?(getStringMatrix)).nil?
	raise "getStringMatrix already defined!"
getStringMatrix = putStringMatrix
getStringMatrixType = getStringMatrix[0][0].class
print(getStringMatrixType)
print(getStringMatrix)
if(getStringMatrixType != 'string')
	raise "getStringMatrix not string!"

print("getStringMatrixWithNull")
if (defined?(getStringMatrixWithNull)).nil?
	raise "getStringMatrixWithNull already defined!"
getStringMatrixWithNull = putStringMatrixWithNull
getStringMatrixWithNullType = getStringMatrixWithNull[0][1].class
print(getStringMatrixWithNullType)
print(getStringMatrixWithNull)
if(getStringMatrixWithNullType != 'string')
	raise "getStringMatrixWithNull not string!"
if(getStringMatrixWithNull[0][0] != null)
	raise "getStringMatrixWithNull[0][0] not null!"
if(getStringMatrixWithNull[1][1] != null)
	raise "getStringMatrixWithNull[1][1] not null!"
if(getStringMatrixWithNull[2][2] != null)
	raise "getStringMatrixWithNull[2][2] not null!"

print("getStringMatrixAsList")
if (defined?(getStringMatrixAsList)).nil?
	raise "getStringMatrixAsList already defined!"
getStringMatrixAsList = putStringMatrixAsList
getStringMatrixAsListType = getStringMatrixAsList[0][0].class
print(getStringMatrixAsListType)
print(getStringMatrixAsList)
if(getStringMatrixAsListType != 'string')
	raise "getStringMatrixAsList not string!"

print("getStringMatrixAsListWithNull")
if (defined?(getStringMatrixAsListWithNull)).nil?
	raise "getStringMatrixAsListWithNull already defined!"
getStringMatrixAsListWithNull = putStringMatrixAsListWithNull
getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1].class
print(getStringMatrixAsListWithNullType)
print(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType != 'string')
	raise "getStringMatrixAsListWithNull not string!"
if(getStringMatrixAsListWithNull[0][0] != null)
	raise "getStringMatrixAsListWithNull[0][0] not null!"
if(getStringMatrixAsListWithNull[1][1] != null)
	raise "getStringMatrixAsListWithNull[1][1] not null!"
if(getStringMatrixAsListWithNull[2][2] != null)
	raise "getStringMatrixAsListWithNull[2][2] not null!"

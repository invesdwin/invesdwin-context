print("getString")
if(typeof getString !== 'undefined')
	throw "getString already defined!"
getString = callback("getString")
getStringType = typeof getString
print(getStringType)
print(getString)
if(getStringType !== 'string')
	throw "getString not string!"
callback("setString",getString)

print("getStringWithNull")
if(typeof getStringWithNull !== 'undefined')
	throw "getStringWithNull already defined!"
getStringWithNull = callback("getStringWithNull")
getStringWithNullType = typeof getStringWithNull
print(getStringWithNullType)
print(getStringWithNull)
if(getStringWithNull !== null)
	throw "getStringWithNull not null!"
callback("setStringWithNull",getStringWithNull)

print("getStringVector")
if(typeof getStringVector !== 'undefined')
	throw "getStringVector already defined!"
getStringVector = callback("getStringVector")
getStringVectorType = typeof getStringVector[0]
print(getStringVectorType)
print(getStringVector)
if(getStringVectorType !== 'string')
	throw "getStringVector not string!"
callback("setStringVector",getStringVector)

print("getStringVectorWithNull")
if(typeof getStringVectorWithNull !== 'undefined')
	throw "getStringVectorWithNull already defined!"
getStringVectorWithNull = callback("getStringVectorWithNull")
getStringVectorWithNullType = typeof getStringVectorWithNull[0]
print(getStringVectorWithNullType)
print(getStringVectorWithNull)
if(getStringVectorWithNullType !== 'string')
	throw "getStringVectorWithNull not string!"
if(getStringVectorWithNull[1] !== null)
	throw "getStringVectorWithNull[1] not null!"
callback("setStringVectorWithNull",getStringVectorWithNull)

print("getStringVectorAsList")
if(typeof getStringVectorAsList !== 'undefined')
	throw "getStringVectorAsList already defined!"
getStringVectorAsList = callback("getStringVectorAsList")
getStringVectorAsListType = typeof getStringVectorAsList[0]
print(getStringVectorAsListType)
print(getStringVectorAsList)
if(getStringVectorAsListType !== 'string')
	throw "getStringVectorAsList not string!"
callback("setStringVectorAsList",getStringVectorAsList)

print("getStringVectorAsListWithNull")
if(typeof getStringVectorAsListWithNull !== 'undefined')
	throw "getStringVectorAsListWithNull already defined!"
getStringVectorAsListWithNull = callback("getStringVectorAsListWithNull")
getStringVectorAsListWithNullType = typeof getStringVectorAsListWithNull[0]
print(getStringVectorAsListWithNullType)
print(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType !== 'string')
	throw "getStringVectorAsListWithNull not string!"
if(getStringVectorAsListWithNull[1] !== null)
	throw "getStringVectorAsListWithNull[1] not null!"
callback("setStringVectorAsListWithNull",getStringVectorAsListWithNull)

print("getStringMatrix")
if(typeof getStringMatrix !== 'undefined')
	throw "getStringMatrix already defined!"
getStringMatrix = callback("getStringMatrix")
getStringMatrixType = typeof getStringMatrix[0][0]
print(getStringMatrixType)
print(getStringMatrix)
if(getStringMatrixType !== 'string')
	throw "getStringMatrix not string!"
callback("setStringMatrix",getStringMatrix)

print("getStringMatrixWithNull")
if(typeof getStringMatrixWithNull !== 'undefined')
	throw "getStringMatrixWithNull already defined!"
getStringMatrixWithNull = callback("getStringMatrixWithNull")
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
callback("setStringMatrixWithNull",getStringMatrixWithNull)

print("getStringMatrixAsList")
if(typeof getStringMatrixAsList !== 'undefined')
	throw "getStringMatrixAsList already defined!"
getStringMatrixAsList = callback("getStringMatrixAsList")
getStringMatrixAsListType = typeof getStringMatrixAsList[0][0]
print(getStringMatrixAsListType)
print(getStringMatrixAsList)
if(getStringMatrixAsListType !== 'string')
	throw "getStringMatrixAsList not string!"
callback("setStringMatrixAsList",getStringMatrixAsList)

print("getStringMatrixAsListWithNull")
if(typeof getStringMatrixAsListWithNull !== 'undefined')
	throw "getStringMatrixAsListWithNull already defined!"
getStringMatrixAsListWithNull = callback("getStringMatrixAsListWithNull")
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
callback("setStringMatrixAsListWithNull",getStringMatrixAsListWithNull)

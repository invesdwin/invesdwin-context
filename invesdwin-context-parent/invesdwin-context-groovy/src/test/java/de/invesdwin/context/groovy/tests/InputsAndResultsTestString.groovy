println("getString")
if(binding.hasVariable('getString'))
	throw new Exception("getString already defined!")
getString = putString
getStringType = getString.getClass()
println(getStringType)
println(getString)
if(getStringType is not unicode):
	throw new Exception("getString not String!")

println("getStringWithNull")
if(binding.hasVariable('getStringWithNull'))
	throw new Exception("getStringWithNull already defined!")
getStringWithNull = putStringWithNull
getStringWithNullType = getStringWithNull.getClass()
println(getStringWithNullType)
println(getStringWithNull)
if(getStringWithNull is not None):
	throw new Exception("getStringWithNull not None!")

println("getStringVector")
if(binding.hasVariable('getStringVector'))
	throw new Exception("getStringVector already defined!")
getStringVector = putStringVector
getStringVectorType = getStringVector[0].getClass()
println(getStringVectorType)
println(getStringVector)
if(getStringVectorType is not unicode):
	throw new Exception("getStringVector not String!")

println("getStringVectorWithNull")
if(binding.hasVariable('getStringVectorWithNull'))
	throw new Exception("getStringVectorWithNull already defined!")
getStringVectorWithNull = putStringVectorWithNull
getStringVectorWithNullType = getStringVectorWithNull[0].getClass()
println(getStringVectorWithNullType)
println(getStringVectorWithNull)
if(getStringVectorWithNullType is not unicode):
	throw new Exception("getStringVectorWithNull not String!")
if(getStringVectorWithNull[1] is not None):
	throw new Exception("getStringVectorWithNull[2] not None!")

println("getStringVectorAsList")
if(binding.hasVariable('getStringVectorAsList'))
	throw new Exception("getStringVectorAsList already defined!")
getStringVectorAsList = putStringVectorAsList
getStringVectorAsListType = getStringVectorAsList[0].getClass()
println(getStringVectorAsListType)
println(getStringVectorAsList)
if(getStringVectorAsListType is not unicode):
	throw new Exception("getStringVectorAsList not String!")

println("getStringVectorAsListWithNull")
if(binding.hasVariable('getStringVectorAsListWithNull'))
	throw new Exception("getStringVectorAsListWithNull already defined!")
getStringVectorAsListWithNull = putStringVectorAsListWithNull
getStringVectorAsListWithNullType = getStringVectorAsListWithNull[0].getClass()
println(getStringVectorAsListWithNullType)
println(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType is not unicode):
	throw new Exception("getStringVectorAsListWithNull not String!")
if(getStringVectorAsListWithNull[1] is not None):
	throw new Exception("getStringVectorAsListWithNull[1] not None!")

println("getStringMatrix")
if(binding.hasVariable('getStringMatrix'))
	throw new Exception("getStringMatrix already defined!")
getStringMatrix = putStringMatrix
getStringMatrixType = getStringMatrix[0][0].getClass()
println(getStringMatrixType)
println(getStringMatrix)
if(getStringMatrixType is not unicode):
	throw new Exception("getStringMatrix not String!")

println("getStringMatrixWithNull")
if(binding.hasVariable('getStringMatrixWithNull'))
	throw new Exception("getStringMatrixWithNull already defined!")
getStringMatrixWithNull = putStringMatrixWithNull
getStringMatrixWithNullType = getStringMatrixWithNull[0][1].getClass()
println(getStringMatrixWithNullType)
println(getStringMatrixWithNull)
if(getStringMatrixWithNullType is not unicode):
	throw new Exception("getStringMatrixWithNull not String!")
if(getStringMatrixWithNull[0][0] is not None):
	throw new Exception("getStringMatrixWithNull[0][0] not None!")
if(getStringMatrixWithNull[1][1] is not None):
	throw new Exception("getStringMatrixWithNull[1][1] not None!")
if(getStringMatrixWithNull[2][2] is not None):
	throw new Exception("getStringMatrixWithNull[2][2] not None!")

println("getStringMatrixAsList")
if(binding.hasVariable('getStringMatrixAsList'))
	throw new Exception("getStringMatrixAsList already defined!")
getStringMatrixAsList = putStringMatrixAsList
getStringMatrixAsListType = getStringMatrixAsList[0][0].getClass()
println(getStringMatrixAsListType)
println(getStringMatrixAsList)
if(getStringMatrixAsListType is not unicode):
	throw new Exception("getStringMatrixAsList not String!")

println("getStringMatrixAsListWithNull")
if(binding.hasVariable('getStringMatrixAsListWithNull'))
	throw new Exception("getStringMatrixAsListWithNull already defined!")
getStringMatrixAsListWithNull = putStringMatrixAsListWithNull
getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1].getClass()
println(getStringMatrixAsListWithNullType)
println(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType is not unicode):
	throw new Exception("getStringMatrixAsListWithNull not String!")
if(getStringMatrixAsListWithNull[0][0] is not None):
	throw new Exception("getStringMatrixAsListWithNull[0][0] not None!")
if(getStringMatrixAsListWithNull[1][1] is not None):
	throw new Exception("getStringMatrixAsListWithNull[2][2] not None!")
if(getStringMatrixAsListWithNull[2][2] is not None):
	throw new Exception("getStringMatrixAsListWithNull[3][3] not naNone!")

puts("getString")
if (defined?(getString)).nil?
	raise "getString already defined!"
end
getString = putString
getStringType = getString.class
puts(getStringType)
puts(getString)
if(getStringType != 'string')
	raise "getString not string!"
end

puts("getStringWithNull")
if (defined?(getStringWithNull)).nil?
	raise "getStringWithNull already defined!"
end
getStringWithNull = putStringWithNull
getStringWithNullType = getStringWithNull.class
puts(getStringWithNullType)
puts(getStringWithNull)
if(getStringWithNull != null)
	raise "getStringWithNull not null!"
end

puts("getStringVector")
if (defined?(getStringVector)).nil?
	raise "getStringVector already defined!"
end
getStringVector = putStringVector
getStringVectorType = getStringVector[0].class
puts(getStringVectorType)
puts(getStringVector)
if(getStringVectorType != 'string')
	raise "getStringVector not string!"
end

puts("getStringVectorWithNull")
if (defined?(getStringVectorWithNull)).nil?
	raise "getStringVectorWithNull already defined!"
end
getStringVectorWithNull = putStringVectorWithNull
getStringVectorWithNullType = getStringVectorWithNull[0].class
puts(getStringVectorWithNullType)
puts(getStringVectorWithNull)
if(getStringVectorWithNullType != 'string')
	raise "getStringVectorWithNull not string!"
end
if(getStringVectorWithNull[1] != null)
	raise "getStringVectorWithNull[1] not null!"
end

puts("getStringVectorAsList")
if (defined?(getStringVectorAsList)).nil?
	raise "getStringVectorAsList already defined!"
end
getStringVectorAsList = putStringVectorAsList
getStringVectorAsListType = getStringVectorAsList[0].class
puts(getStringVectorAsListType)
puts(getStringVectorAsList)
if(getStringVectorAsListType != 'string')
	raise "getStringVectorAsList not string!"
end

puts("getStringVectorAsListWithNull")
if (defined?(getStringVectorAsListWithNull)).nil?
	raise "getStringVectorAsListWithNull already defined!"
end
getStringVectorAsListWithNull = putStringVectorAsListWithNull
getStringVectorAsListWithNullType = getStringVectorAsListWithNull[0].class
puts(getStringVectorAsListWithNullType)
puts(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType != 'string')
	raise "getStringVectorAsListWithNull not string!"
end
if(getStringVectorAsListWithNull[1] != null)
	raise "getStringVectorAsListWithNull[1] not null!"
end

puts("getStringMatrix")
if (defined?(getStringMatrix)).nil?
	raise "getStringMatrix already defined!"
end
getStringMatrix = putStringMatrix
getStringMatrixType = getStringMatrix[0][0].class
puts(getStringMatrixType)
puts(getStringMatrix)
if(getStringMatrixType != 'string')
	raise "getStringMatrix not string!"
end

puts("getStringMatrixWithNull")
if (defined?(getStringMatrixWithNull)).nil?
	raise "getStringMatrixWithNull already defined!"
end
getStringMatrixWithNull = putStringMatrixWithNull
getStringMatrixWithNullType = getStringMatrixWithNull[0][1].class
puts(getStringMatrixWithNullType)
puts(getStringMatrixWithNull)
if(getStringMatrixWithNullType != 'string')
	raise "getStringMatrixWithNull not string!"
end
if(getStringMatrixWithNull[0][0] != null)
	raise "getStringMatrixWithNull[0][0] not null!"
end
if(getStringMatrixWithNull[1][1] != null)
	raise "getStringMatrixWithNull[1][1] not null!"
end
if(getStringMatrixWithNull[2][2] != null)
	raise "getStringMatrixWithNull[2][2] not null!"
end

puts("getStringMatrixAsList")
if (defined?(getStringMatrixAsList)).nil?
	raise "getStringMatrixAsList already defined!"
end
getStringMatrixAsList = putStringMatrixAsList
getStringMatrixAsListType = getStringMatrixAsList[0][0].class
puts(getStringMatrixAsListType)
puts(getStringMatrixAsList)
if(getStringMatrixAsListType != 'string')
	raise "getStringMatrixAsList not string!"
end

puts("getStringMatrixAsListWithNull")
if (defined?(getStringMatrixAsListWithNull)).nil?
	raise "getStringMatrixAsListWithNull already defined!"
end
getStringMatrixAsListWithNull = putStringMatrixAsListWithNull
getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1].class
puts(getStringMatrixAsListWithNullType)
puts(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType != 'string')
	raise "getStringMatrixAsListWithNull not string!"
end
if(getStringMatrixAsListWithNull[0][0] != null)
	raise "getStringMatrixAsListWithNull[0][0] not null!"
end
if(getStringMatrixAsListWithNull[1][1] != null)
	raise "getStringMatrixAsListWithNull[1][1] not null!"
end
if(getStringMatrixAsListWithNull[2][2] != null)
	raise "getStringMatrixAsListWithNull[2][2] not null!"
end

puts("getString")
unless (defined?(getString)).nil? || getString.nil?
	raise "getString already defined!"
end
getString = callback("getString")
getStringType = getString.class
puts(getStringType)
puts(getString)
if(getStringType != String)
	raise "getString not String!"
end
callback("setString", getString)

puts("getStringWithNull")
unless (defined?(getStringWithNull)).nil? || getStringWithNull.nil?
	raise "getStringWithNull already defined!"
end
getStringWithNull = callback("getStringWithNull")
getStringWithNullType = getStringWithNull.class
puts(getStringWithNullType)
puts(getStringWithNull)
unless(getStringWithNull.nil?)
	raise "getStringWithNull not null!"
end
callback("setStringWithNull", getStringWithNull)

puts("getStringVector")
unless (defined?(getStringVector)).nil? || getStringVector.nil?
	raise "getStringVector already defined!"
end
getStringVector = callback("getStringVector")
getStringVectorType = getStringVector[0].class
puts(getStringVectorType)
puts(getStringVector)
if(getStringVectorType != String)
	raise "getStringVector not String!"
end
callback("setStringVector", getStringVector)

puts("getStringVectorWithNull")
unless (defined?(getStringVectorWithNull)).nil? || getStringVectorWithNull.nil?
	raise "getStringVectorWithNull already defined!"
end
getStringVectorWithNull = callback("getStringVectorWithNull")
getStringVectorWithNullType = getStringVectorWithNull[0].class
puts(getStringVectorWithNullType)
puts(getStringVectorWithNull)
if(getStringVectorWithNullType != String)
	raise "getStringVectorWithNull not String!"
end
unless(getStringVectorWithNull[1].nil?)
	raise "getStringVectorWithNull[1] not null!"
end
callback("setStringVectorWithNull", getStringVectorWithNull)

puts("getStringVectorAsList")
unless (defined?(getStringVectorAsList)).nil? || getStringVectorAsList.nil?
	raise "getStringVectorAsList already defined!"
end
getStringVectorAsList = callback("getStringVectorAsList")
getStringVectorAsListType = getStringVectorAsList[0].class
puts(getStringVectorAsListType)
puts(getStringVectorAsList)
if(getStringVectorAsListType != String)
	raise "getStringVectorAsList not String!"
end
callback("setStringVectorAsList", getStringVectorAsList)

puts("getStringVectorAsListWithNull")
unless (defined?(getStringVectorAsListWithNull)).nil? || getStringVectorAsListWithNull.nil?
	raise "getStringVectorAsListWithNull already defined!"
end
getStringVectorAsListWithNull = callback("getStringVectorAsListWithNull")
getStringVectorAsListWithNullType = getStringVectorAsListWithNull[0].class
puts(getStringVectorAsListWithNullType)
puts(getStringVectorAsListWithNull)
if(getStringVectorAsListWithNullType != String)
	raise "getStringVectorAsListWithNull not String!"
end
unless(getStringVectorAsListWithNull[1].nil?)
	raise "getStringVectorAsListWithNull[1] not null!"
end
callback("setStringVectorAsListWithNull", getStringVectorAsListWithNull)

puts("getStringMatrix")
unless (defined?(getStringMatrix)).nil? || getStringMatrix.nil?
	raise "getStringMatrix already defined!"
end
getStringMatrix = callback("getStringMatrix")
getStringMatrixType = getStringMatrix[0][0].class
puts(getStringMatrixType)
puts(getStringMatrix)
if(getStringMatrixType != String)
	raise "getStringMatrix not String!"
end
callback("setStringMatrix", getStringMatrix)

puts("getStringMatrixWithNull")
unless (defined?(getStringMatrixWithNull)).nil? || getStringMatrixWithNull.nil?
	raise "getStringMatrixWithNull already defined!"
end
getStringMatrixWithNull = callback("getStringMatrixWithNull")
getStringMatrixWithNullType = getStringMatrixWithNull[0][1].class
puts(getStringMatrixWithNullType)
puts(getStringMatrixWithNull)
if(getStringMatrixWithNullType != String)
	raise "getStringMatrixWithNull not String!"
end
unless(getStringMatrixWithNull[0][0].nil?)
	raise "getStringMatrixWithNull[0][0] not null!"
end
unless(getStringMatrixWithNull[1][1].nil?)
	raise "getStringMatrixWithNull[1][1] not null!"
end
unless(getStringMatrixWithNull[2][2].nil?)
	raise "getStringMatrixWithNull[2][2] not null!"
end
callback("setStringMatrixWithNull", getStringMatrixWithNull)

puts("getStringMatrixAsList")
unless (defined?(getStringMatrixAsList)).nil? || getStringMatrixAsList.nil?
	raise "getStringMatrixAsList already defined!"
end
getStringMatrixAsList = callback("getStringMatrixAsList")
getStringMatrixAsListType = getStringMatrixAsList[0][0].class
puts(getStringMatrixAsListType)
puts(getStringMatrixAsList)
if(getStringMatrixAsListType != String)
	raise "getStringMatrixAsList not String!"
end
callback("setStringMatrixAsList", getStringMatrixAsList)

puts("getStringMatrixAsListWithNull")
unless (defined?(getStringMatrixAsListWithNull)).nil? || getStringMatrixAsListWithNull.nil?
	raise "getStringMatrixAsListWithNull already defined!"
end
getStringMatrixAsListWithNull = callback("getStringMatrixAsListWithNull")
getStringMatrixAsListWithNullType = getStringMatrixAsListWithNull[0][1].class
puts(getStringMatrixAsListWithNullType)
puts(getStringMatrixAsListWithNull)
if(getStringMatrixAsListWithNullType != String)
	raise "getStringMatrixAsListWithNull not String!"
end
unless(getStringMatrixAsListWithNull[0][0].nil?)
	raise "getStringMatrixAsListWithNull[0][0] not null!"
end
unless(getStringMatrixAsListWithNull[1][1].nil?)
	raise "getStringMatrixAsListWithNull[1][1] not null!"
end
unless(getStringMatrixAsListWithNull[2][2].nil?)
	raise "getStringMatrixAsListWithNull[2][2] not null!"
end
callback("setStringMatrixAsListWithNull", getStringMatrixAsListWithNull)

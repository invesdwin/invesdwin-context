puts("getFloat")
if (defined?(getFloat)).nil?
	raise "getFloat already defined!"
end
getFloat = putFloat
getFloatType = getFloat.class
puts(getFloatType)
puts(getFloat)
if(getFloatType != 'number')
	raise "getFloat not number!"
end

puts("getFloatVector")
if (defined?(getFloatVector)).nil?
	raise "getFloatVector already defined!"
end
getFloatVector = putFloatVector
getFloatVectorType = getFloatVector[0].class
puts(getFloatVectorType)
puts(getFloatVector)
if(getFloatVectorType != 'number')
	raise "getFloatVector not number!"
end

puts("getFloatVectorAsList")
if (defined?(getFloatVectorAsList)).nil?
	raise "getFloatVectorAsList already defined!"
end
getFloatVectorAsList = putFloatVectorAsList
getFloatVectorAsListType = getFloatVectorAsList[0].class
puts(getFloatVectorAsListType)
puts(getFloatVectorAsList)
if(getFloatVectorAsListType != 'number')
	raise "getFloatVectorAsList not number!"
end

puts("getFloatMatrix")
if (defined?(getFloatMatrix)).nil?
	raise "getFloatMatrix already defined!"
end
getFloatMatrix = putFloatMatrix
getFloatMatrixType = getFloatMatrix[0][0].class
puts(getFloatMatrixType)
puts(getFloatMatrix)
if(getFloatMatrixType != 'number')
	raise "getFloatMatrix not number!"
end

puts("getFloatMatrixAsList")
if (defined?(getFloatMatrixAsList)).nil?
	raise "getFloatMatrixAsList already defined!"
end
getFloatMatrixAsList = putFloatMatrixAsList
getFloatMatrixAsListType = getFloatMatrixAsList[0][0].class
puts(getFloatMatrixAsListType)
puts(getFloatMatrixAsList)
if(getFloatMatrixAsListType != 'number')
	raise "getFloatMatrixAsList not number!"
end

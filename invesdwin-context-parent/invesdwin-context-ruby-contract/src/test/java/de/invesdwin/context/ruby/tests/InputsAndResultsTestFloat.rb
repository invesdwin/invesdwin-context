puts("getFloat")
unless (defined?(getFloat)).nil? || getFloat.nil?
	raise "getFloat already defined!"
end
getFloat = putFloat
getFloatType = getFloat.class
puts(getFloatType)
puts(getFloat)
if(getFloatType != Float)
	raise "getFloat not Float!"
end

puts("getFloatVector")
unless (defined?(getFloatVector)).nil? || getFloatVector.nil?
	raise "getFloatVector already defined!"
end
getFloatVector = putFloatVector
getFloatVectorType = getFloatVector[0].class
puts(getFloatVectorType)
puts(getFloatVector)
if(getFloatVectorType != Float)
	raise "getFloatVector not Float!"
end

puts("getFloatVectorAsList")
unless (defined?(getFloatVectorAsList)).nil? || getFloatVectorAsList.nil?
	raise "getFloatVectorAsList already defined!"
end
getFloatVectorAsList = putFloatVectorAsList
getFloatVectorAsListType = getFloatVectorAsList[0].class
puts(getFloatVectorAsListType)
puts(getFloatVectorAsList)
if(getFloatVectorAsListType != Float)
	raise "getFloatVectorAsList not Float!"
end

puts("getFloatMatrix")
unless (defined?(getFloatMatrix)).nil? || getFloatMatrix.nil?
	raise "getFloatMatrix already defined!"
end
getFloatMatrix = putFloatMatrix
getFloatMatrixType = getFloatMatrix[0][0].class
puts(getFloatMatrixType)
puts(getFloatMatrix)
if(getFloatMatrixType != Float)
	raise "getFloatMatrix not Float!"
end

puts("getFloatMatrixAsList")
unless (defined?(getFloatMatrixAsList)).nil? || getFloatMatrixAsList.nil?
	raise "getFloatMatrixAsList already defined!"
end
getFloatMatrixAsList = putFloatMatrixAsList
getFloatMatrixAsListType = getFloatMatrixAsList[0][0].class
puts(getFloatMatrixAsListType)
puts(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float)
	raise "getFloatMatrixAsList not Float!"
end

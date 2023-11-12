puts("getFloat")
unless (defined?(getFloat)).nil? || getFloat.nil?
	raise "getFloat already defined!"
end
getFloat = callback("getFloat")
getFloatType = getFloat.class
puts(getFloatType)
puts(getFloat)
if(getFloatType != Float)
	raise "getFloat not Float!"
end
callback("setFloat", getFloat)

puts("getFloatVector")
unless (defined?(getFloatVector)).nil? || getFloatVector.nil?
	raise "getFloatVector already defined!"
end
getFloatVector = callback("getFloatVector")
getFloatVectorType = getFloatVector[0].class
puts(getFloatVectorType)
puts(getFloatVector)
if(getFloatVectorType != Float)
	raise "getFloatVector not Float!"
end
callback("setFloatVector", getFloatVector)

puts("getFloatVectorAsList")
unless (defined?(getFloatVectorAsList)).nil? || getFloatVectorAsList.nil?
	raise "getFloatVectorAsList already defined!"
end
getFloatVectorAsList = callback("getFloatVectorAsList")
getFloatVectorAsListType = getFloatVectorAsList[0].class
puts(getFloatVectorAsListType)
puts(getFloatVectorAsList)
if(getFloatVectorAsListType != Float)
	raise "getFloatVectorAsList not Float!"
end
callback("setFloatVectorAsList", getFloatVectorAsList)

puts("getFloatMatrix")
unless (defined?(getFloatMatrix)).nil? || getFloatMatrix.nil?
	raise "getFloatMatrix already defined!"
end
getFloatMatrix = callback("getFloatMatrix")
getFloatMatrixType = getFloatMatrix[0][0].class
puts(getFloatMatrixType)
puts(getFloatMatrix)
if(getFloatMatrixType != Float)
	raise "getFloatMatrix not Float!"
end
callback("setFloatMatrix", getFloatMatrix)

puts("getFloatMatrixAsList")
unless (defined?(getFloatMatrixAsList)).nil? || getFloatMatrixAsList.nil?
	raise "getFloatMatrixAsList already defined!"
end
getFloatMatrixAsList = callback("getFloatMatrixAsList")
getFloatMatrixAsListType = getFloatMatrixAsList[0][0].class
puts(getFloatMatrixAsListType)
puts(getFloatMatrixAsList)
if(getFloatMatrixAsListType != Float)
	raise "getFloatMatrixAsList not Float!"
end
callback("setFloatMatrixAsList", getFloatMatrixAsList)

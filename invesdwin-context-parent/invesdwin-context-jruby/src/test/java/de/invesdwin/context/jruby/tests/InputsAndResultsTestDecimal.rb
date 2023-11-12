puts("getDecimal")
unless (defined?(getDecimal)).nil? || getDecimal.nil?
	raise "getDecimal already defined!"
end
getDecimal = putDecimal
getDecimalType = getDecimal.class
puts(getDecimalType)
puts(getDecimal)
if(getDecimalType != Float)
	raise "getDecimal not Float!"
end

puts("getDecimalVector")
unless (defined?(getDecimalVector)).nil? || getDecimalVector.nil?
	raise "getDecimalVector already defined!"
end
getDecimalVector = putDecimalVector
getDecimalVectorType = getDecimalVector[0].class
puts(getDecimalVectorType)
puts(getDecimalVector)
if(getDecimalVectorType != Float)
	raise "getDecimalVector not Float!"
end

puts("getDecimalVectorAsList")
unless (defined?(getDecimalVectorAsList)).nil? || getDecimalVectorAsList.nil?
	raise "getDecimalVectorAsList already defined!"
end
getDecimalVectorAsList = putDecimalVectorAsList
getDecimalVectorAsListType = getDecimalVectorAsList[0].class
puts(getDecimalVectorAsListType)
puts(getDecimalVectorAsList)
if(getDecimalVectorAsListType != Float)
	raise "getDecimalVectorAsList not Float!"
end

puts("getDecimalMatrix")
unless (defined?(getDecimalMatrix)).nil? || getDecimalMatrix.nil?
	raise "getDecimalMatrix already defined!"
end
getDecimalMatrix = putDecimalMatrix
getDecimalMatrixType = getDecimalMatrix[0][0].class
puts(getDecimalMatrixType)
puts(getDecimalMatrix)
if(getDecimalMatrixType != Float)
	raise "getDecimalMatrix not Float!"
end

puts("getDecimalMatrixAsList")
unless (defined?(getDecimalMatrixAsList)).nil? || getDecimalMatrixAsList.nil?
	raise "getDecimalMatrixAsList already defined!"
end
getDecimalMatrixAsList = putDecimalMatrixAsList
getDecimalMatrixAsListType = getDecimalMatrixAsList[0][0].class
puts(getDecimalMatrixAsListType)
puts(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != Float)
	raise "getDecimalMatrixAsList not Float!"
end

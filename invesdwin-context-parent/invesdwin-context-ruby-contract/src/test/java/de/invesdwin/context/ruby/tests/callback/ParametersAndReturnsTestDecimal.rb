puts("getDecimal")
unless (defined?(getDecimal)).nil? || getDecimal.nil?
	raise "getDecimal already defined!"
end
getDecimal = callback("getDecimal")
getDecimalType = getDecimal.class
puts(getDecimalType)
puts(getDecimal)
if(getDecimalType != Float)
	raise "getDecimal not Float!"
end
callback("setDecimal", getDecimal)

puts("getDecimalVector")
unless (defined?(getDecimalVector)).nil? || getDecimalVector.nil?
	raise "getDecimalVector already defined!"
end
getDecimalVector = callback("getDecimalVector")
getDecimalVectorType = getDecimalVector[0].class
puts(getDecimalVectorType)
puts(getDecimalVector)
if(getDecimalVectorType != Float)
	raise "getDecimalVector not Float!"
end
callback("setDecimalVector", getDecimalVector)

puts("getDecimalVectorAsList")
unless (defined?(getDecimalVectorAsList)).nil? || getDecimalVectorAsList.nil?
	raise "getDecimalVectorAsList already defined!"
end
getDecimalVectorAsList = callback("getDecimalVectorAsList")
getDecimalVectorAsListType = getDecimalVectorAsList[0].class
puts(getDecimalVectorAsListType)
puts(getDecimalVectorAsList)
if(getDecimalVectorAsListType != Float)
	raise "getDecimalVectorAsList not Float!"
end
callback("setDecimalVectorAsList", getDecimalVectorAsList)

puts("getDecimalMatrix")
unless (defined?(getDecimalMatrix)).nil? || getDecimalMatrix.nil?
	raise "getDecimalMatrix already defined!"
end
getDecimalMatrix = callback("getDecimalMatrix")
getDecimalMatrixType = getDecimalMatrix[0][0].class
puts(getDecimalMatrixType)
puts(getDecimalMatrix)
if(getDecimalMatrixType != Float)
	raise "getDecimalMatrix not Float!"
end
callback("setDecimalMatrix", getDecimalMatrix)

puts("getDecimalMatrixAsList")
unless (defined?(getDecimalMatrixAsList)).nil? || getDecimalMatrixAsList.nil?
	raise "getDecimalMatrixAsList already defined!"
end
getDecimalMatrixAsList = callback("getDecimalMatrixAsList")
getDecimalMatrixAsListType = getDecimalMatrixAsList[0][0].class
puts(getDecimalMatrixAsListType)
puts(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != Float)
	raise "getDecimalMatrixAsList not Float!"
end
callback("setDecimalMatrixAsList", getDecimalMatrixAsList)

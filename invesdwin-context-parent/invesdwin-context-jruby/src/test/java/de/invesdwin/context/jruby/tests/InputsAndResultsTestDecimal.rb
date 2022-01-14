puts("getDecimal")
if (defined?(getDecimal)).nil?
	raise "getDecimal already defined!"
getDecimal = putDecimal
getDecimalType = getDecimal.class
puts(getDecimalType)
puts(getDecimal)
if(getDecimalType != 'number')
	raise "getDecimal not number!"
end

puts("getDecimalVector")
if (defined?(getDecimalVector)).nil?
	raise "getDecimalVector already defined!"
end
getDecimalVector = putDecimalVector
getDecimalVectorType = getDecimalVector[0].class
puts(getDecimalVectorType)
puts(getDecimalVector)
if(getDecimalVectorType != 'number')
	raise "getDecimalVector not number!"
end

puts("getDecimalVectorAsList")
if (defined?(getDecimalVectorAsList)).nil?
	raise "getDecimalVectorAsList already defined!"
end
getDecimalVectorAsList = putDecimalVectorAsList
getDecimalVectorAsListType = getDecimalVectorAsList[0].class
puts(getDecimalVectorAsListType)
puts(getDecimalVectorAsList)
if(getDecimalVectorAsListType != 'number')
	raise "getDecimalVectorAsList not number!"
end

puts("getDecimalMatrix")
if (defined?(getDecimalMatrix)).nil?
	raise "getDecimalMatrix already defined!"
end
getDecimalMatrix = putDecimalMatrix
getDecimalMatrixType = getDecimalMatrix[0][0].class
puts(getDecimalMatrixType)
puts(getDecimalMatrix)
if(getDecimalMatrixType != 'number')
	raise "getDecimalMatrix not number!"
end

puts("getDecimalMatrixAsList")
if (defined?(getDecimalMatrixAsList)).nil?
	raise "getDecimalMatrixAsList already defined!"
end
getDecimalMatrixAsList = putDecimalMatrixAsList
getDecimalMatrixAsListType = getDecimalMatrixAsList[0][0].class
puts(getDecimalMatrixAsListType)
puts(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != 'number')
	raise "getDecimalMatrixAsList not number!"
end

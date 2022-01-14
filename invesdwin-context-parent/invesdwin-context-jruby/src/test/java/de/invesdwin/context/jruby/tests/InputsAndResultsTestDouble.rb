puts("getDouble")
unless (defined?(getDouble)).nil?
	raise "getDouble already defined!"
end
getDouble = putDouble
getDoubleType = getDouble.class
puts(getDoubleType)
puts(getDouble)
if(getDoubleType != 'number')
	raise "getDouble not number!"
end

puts("getDoubleVector")
unless (defined?(getDoubleVector)).nil?
	raise "getDoubleVector already defined!"
end
getDoubleVector = putDoubleVector
getDoubleVectorType = getDoubleVector[0].class
puts(getDoubleVectorType)
puts(getDoubleVector)
if(getDoubleVectorType != 'number')
	raise "getDoubleVector not number!"
end

puts("getDoubleVectorAsList")
unless (defined?(getDoubleVectorAsList)).nil?
	raise "getDoubleVectorAsList already defined!"
end
getDoubleVectorAsList = putDoubleVectorAsList
getDoubleVectorAsListType = getDoubleVectorAsList[0].class
puts(getDoubleVectorAsListType)
puts(getDoubleVectorAsList)
if(getDoubleVectorAsListType != 'number')
	raise "getDoubleVectorAsList not number!"
end

puts("getDoubleMatrix")
unless (defined?(getDoubleMatrix)).nil?
	raise "getDoubleMatrix already defined!"
end
getDoubleMatrix = putDoubleMatrix
getDoubleMatrixType = getDoubleMatrix[0][0].class
puts(getDoubleMatrixType)
puts(getDoubleMatrix)
if(getDoubleMatrixType != 'number')
	raise "getDoubleMatrix not number!"
end

puts("getDoubleMatrixAsList")
unless (defined?(getDoubleMatrixAsList)).nil?
	raise "getDoubleMatrixAsList already defined!"
end
getDoubleMatrixAsList = putDoubleMatrixAsList
getDoubleMatrixAsListType = getDoubleMatrixAsList[0][0].class
puts(getDoubleMatrixAsListType)
puts(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != 'number')
	raise "getDoubleMatrixAsList not number!"
end

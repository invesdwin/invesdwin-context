puts("getDouble")
unless (defined?(getDouble)).nil?
	raise "getDouble already defined!"
end
getDouble = putDouble
getDoubleType = getDouble.class
puts(getDoubleType)
puts(getDouble)
if(getDoubleType != Float)
	raise "getDouble not Float!"
end

puts("getDoubleVector")
unless (defined?(getDoubleVector)).nil?
	raise "getDoubleVector already defined!"
end
getDoubleVector = putDoubleVector
getDoubleVectorType = getDoubleVector[0].class
puts(getDoubleVectorType)
puts(getDoubleVector)
if(getDoubleVectorType != Float)
	raise "getDoubleVector not Float!"
end

puts("getDoubleVectorAsList")
unless (defined?(getDoubleVectorAsList)).nil?
	raise "getDoubleVectorAsList already defined!"
end
getDoubleVectorAsList = putDoubleVectorAsList
getDoubleVectorAsListType = getDoubleVectorAsList[0].class
puts(getDoubleVectorAsListType)
puts(getDoubleVectorAsList)
if(getDoubleVectorAsListType != Float)
	raise "getDoubleVectorAsList not Float!"
end

puts("getDoubleMatrix")
unless (defined?(getDoubleMatrix)).nil?
	raise "getDoubleMatrix already defined!"
end
getDoubleMatrix = putDoubleMatrix
getDoubleMatrixType = getDoubleMatrix[0][0].class
puts(getDoubleMatrixType)
puts(getDoubleMatrix)
if(getDoubleMatrixType != Float)
	raise "getDoubleMatrix not Float!"
end

puts("getDoubleMatrixAsList")
unless (defined?(getDoubleMatrixAsList)).nil?
	raise "getDoubleMatrixAsList already defined!"
end
getDoubleMatrixAsList = putDoubleMatrixAsList
getDoubleMatrixAsListType = getDoubleMatrixAsList[0][0].class
puts(getDoubleMatrixAsListType)
puts(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != Float)
	raise "getDoubleMatrixAsList not Float!"
end

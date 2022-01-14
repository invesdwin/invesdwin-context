puts("getInteger")
if (defined?(getInteger)).nil?
	raise "getInteger already defined!"
end
getInteger = putInteger
getIntegerType = getInteger.class
puts(getIntegerType)
puts(getInteger)
if(getIntegerType != 'number')
	raise "getInteger not number!"
end

puts("getIntegerVector")
if (defined?(getIntegerVector)).nil?
	raise "getIntegerVector already defined!"
end
getIntegerVector = putIntegerVector
getIntegerVectorType = getIntegerVector[0].class
puts(getIntegerVectorType)
puts(getIntegerVector)
if(getIntegerVectorType != 'number')
	raise "getIntegerVector not number!"
end

puts("getIntegerVectorAsList")
if (defined?(getIntegerVectorAsList)).nil?
	raise "getIntegerVectorAsList already defined!"
end
getIntegerVectorAsList = putIntegerVectorAsList
getIntegerVectorAsListType = getIntegerVectorAsList[0].class
puts(getIntegerVectorAsListType)
puts(getIntegerVectorAsList)
if(getIntegerVectorAsListType != 'number')
	raise "getIntegerVectorAsList not number!"
end

puts("getIntegerMatrix")
if (defined?(getIntegerMatrix)).nil?
	raise "getIntegerMatrix already defined!"
end
getIntegerMatrix = putIntegerMatrix
getIntegerMatrixType = getIntegerMatrix[0][0].class
puts(getIntegerMatrixType)
puts(getIntegerMatrix)
if(getIntegerMatrixType != 'number')
	raise "getIntegerMatrix not number!"
end

puts("getIntegerMatrixAsList")
if (defined?(getIntegerMatrixAsList)).nil?
	raise "getIntegerMatrixAsList already defined!"
end
getIntegerMatrixAsList = putIntegerMatrixAsList
getIntegerMatrixAsListType = getIntegerMatrixAsList[0][0].class
puts(getIntegerMatrixAsListType)
puts(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType != 'number')
	raise "getIntegerMatrixAsList not number!"
end

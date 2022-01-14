puts("getInteger")
unless (defined?(getInteger)).nil?
	raise "getInteger already defined!"
end
getInteger = putInteger
getIntegerType = getInteger.class
puts(getIntegerType)
puts(getInteger)
if(getIntegerType != Integer)
	raise "getInteger not Integer!"
end

puts("getIntegerVector")
unless (defined?(getIntegerVector)).nil?
	raise "getIntegerVector already defined!"
end
getIntegerVector = putIntegerVector
getIntegerVectorType = getIntegerVector[0].class
puts(getIntegerVectorType)
puts(getIntegerVector)
if(getIntegerVectorType != Integer)
	raise "getIntegerVector not Integer!"
end

puts("getIntegerVectorAsList")
unless (defined?(getIntegerVectorAsList)).nil?
	raise "getIntegerVectorAsList already defined!"
end
getIntegerVectorAsList = putIntegerVectorAsList
getIntegerVectorAsListType = getIntegerVectorAsList[0].class
puts(getIntegerVectorAsListType)
puts(getIntegerVectorAsList)
if(getIntegerVectorAsListType != Integer)
	raise "getIntegerVectorAsList not Integer!"
end

puts("getIntegerMatrix")
unless (defined?(getIntegerMatrix)).nil?
	raise "getIntegerMatrix already defined!"
end
getIntegerMatrix = putIntegerMatrix
getIntegerMatrixType = getIntegerMatrix[0][0].class
puts(getIntegerMatrixType)
puts(getIntegerMatrix)
if(getIntegerMatrixType != Integer)
	raise "getIntegerMatrix not Integer!"
end

puts("getIntegerMatrixAsList")
unless (defined?(getIntegerMatrixAsList)).nil?
	raise "getIntegerMatrixAsList already defined!"
end
getIntegerMatrixAsList = putIntegerMatrixAsList
getIntegerMatrixAsListType = getIntegerMatrixAsList[0][0].class
puts(getIntegerMatrixAsListType)
puts(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType != Integer)
	raise "getIntegerMatrixAsList not Integer!"
end

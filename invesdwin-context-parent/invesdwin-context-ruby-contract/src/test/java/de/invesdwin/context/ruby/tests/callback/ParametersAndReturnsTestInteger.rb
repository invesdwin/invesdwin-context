puts("getInteger")
unless (defined?(getInteger)).nil? || getInteger.nil?
	raise "getInteger already defined!"
end
getInteger = callback("getInteger")
getIntegerType = getInteger.class
puts(getIntegerType)
puts(getInteger)
if(getIntegerType != Integer)
	raise "getInteger not Integer!"
end
callback("setInteger", getInteger)

puts("getIntegerVector")
unless (defined?(getIntegerVector)).nil? || getIntegerVector.nil?
	raise "getIntegerVector already defined!"
end
getIntegerVector = callback("getIntegerVector")
getIntegerVectorType = getIntegerVector[0].class
puts(getIntegerVectorType)
puts(getIntegerVector)
if(getIntegerVectorType != Integer)
	raise "getIntegerVector not Integer!"
end
callback("setIntegerVector", getIntegerVector)

puts("getIntegerVectorAsList")
unless (defined?(getIntegerVectorAsList)).nil? || getIntegerVectorAsList.nil?
	raise "getIntegerVectorAsList already defined!"
end
getIntegerVectorAsList = callback("getIntegerVectorAsList")
getIntegerVectorAsListType = getIntegerVectorAsList[0].class
puts(getIntegerVectorAsListType)
puts(getIntegerVectorAsList)
if(getIntegerVectorAsListType != Integer)
	raise "getIntegerVectorAsList not Integer!"
end
callback("setIntegerVectorAsList", getIntegerVectorAsList)

puts("getIntegerMatrix")
unless (defined?(getIntegerMatrix)).nil? || getIntegerMatrix.nil?
	raise "getIntegerMatrix already defined!"
end
getIntegerMatrix = callback("getIntegerMatrix")
getIntegerMatrixType = getIntegerMatrix[0][0].class
puts(getIntegerMatrixType)
puts(getIntegerMatrix)
if(getIntegerMatrixType != Integer)
	raise "getIntegerMatrix not Integer!"
end
callback("setIntegerMatrix", getIntegerMatrix)

puts("getIntegerMatrixAsList")
unless (defined?(getIntegerMatrixAsList)).nil? || getIntegerMatrixAsList.nil?
	raise "getIntegerMatrixAsList already defined!"
end
getIntegerMatrixAsList = callback("getIntegerMatrixAsList")
getIntegerMatrixAsListType = getIntegerMatrixAsList[0][0].class
puts(getIntegerMatrixAsListType)
puts(getIntegerMatrixAsList)
if(getIntegerMatrixAsListType != Integer)
	raise "getIntegerMatrixAsList not Integer!"
end
callback("setIntegerMatrixAsList", getIntegerMatrixAsList)

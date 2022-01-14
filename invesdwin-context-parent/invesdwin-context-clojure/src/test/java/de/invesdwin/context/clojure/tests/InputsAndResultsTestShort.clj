puts("getShort")
unless (defined?(getShort)).nil?
	raise "getShort already defined!"
end
getShort = putShort
getShortType = getShort.class
puts(getShortType)
puts(getShort)
if(getShortType != Integer)
	raise "getShort not Integer!"
end

puts("getShortVector")
unless (defined?(getShortVector)).nil?
	raise "getShortVector already defined!"
end
getShortVector = putShortVector
getShortVectorType = getShortVector[0].class
puts(getShortVectorType)
puts(getShortVector)
if(getShortVectorType != Integer)
	raise "getShortVector not Integer!"
end

puts("getShortVectorAsList")
unless (defined?(getShortVectorAsList)).nil?
	raise "getShortVectorAsList already defined!"
end
getShortVectorAsList = putShortVectorAsList
getShortVectorAsListType = getShortVectorAsList[0].class
puts(getShortVectorAsListType)
puts(getShortVectorAsList)
if(getShortVectorAsListType != Integer)
	raise "getShortVectorAsList not Integer!"
end

puts("getShortMatrix")
unless (defined?(getShortMatrix)).nil?
	raise "getShortMatrix already defined!"
end
getShortMatrix = putShortMatrix
getShortMatrixType = getShortMatrix[0][0].class
puts(getShortMatrixType)
puts(getShortMatrix)
if(getShortMatrixType != Integer)
	raise "getShortMatrix not Integer!"
end

puts("getShortMatrixAsList")
unless (defined?(getShortMatrixAsList)).nil?
	raise "getShortMatrixAsList already defined!"
end
getShortMatrixAsList = putShortMatrixAsList
getShortMatrixAsListType = getShortMatrixAsList[0][0].class
puts(getShortMatrixAsListType)
puts(getShortMatrixAsList)
if(getShortMatrixAsListType != Integer)
	raise "getShortMatrixAsList not Integer!"
end

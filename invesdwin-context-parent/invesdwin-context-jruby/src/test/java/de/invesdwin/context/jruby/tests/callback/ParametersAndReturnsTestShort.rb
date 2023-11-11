puts("getShort")
unless (defined?(getShort)).nil?
	raise "getShort already defined!"
end
getShort = callback("getShort")
getShortType = getShort.class
puts(getShortType)
puts(getShort)
if(getShortType != Integer)
	raise "getShort not Integer!"
end
callback("setShort", getShort)

puts("getShortVector")
unless (defined?(getShortVector)).nil?
	raise "getShortVector already defined!"
end
getShortVector = callback("getShortVector")
getShortVectorType = getShortVector[0].class
puts(getShortVectorType)
puts(getShortVector)
if(getShortVectorType != Integer)
	raise "getShortVector not Integer!"
end
callback("setShortVector", getShortVector)

puts("getShortVectorAsList")
unless (defined?(getShortVectorAsList)).nil?
	raise "getShortVectorAsList already defined!"
end
getShortVectorAsList = callback("getShortVectorAsList")
getShortVectorAsListType = getShortVectorAsList[0].class
puts(getShortVectorAsListType)
puts(getShortVectorAsList)
if(getShortVectorAsListType != Integer)
	raise "getShortVectorAsList not Integer!"
end
callback("setShortVectorAsList", getShortVectorAsList)

puts("getShortMatrix")
unless (defined?(getShortMatrix)).nil?
	raise "getShortMatrix already defined!"
end
getShortMatrix = callback("getShortMatrix")
getShortMatrixType = getShortMatrix[0][0].class
puts(getShortMatrixType)
puts(getShortMatrix)
if(getShortMatrixType != Integer)
	raise "getShortMatrix not Integer!"
end
callback("setShortMatrix", getShortMatrix)

puts("getShortMatrixAsList")
unless (defined?(getShortMatrixAsList)).nil?
	raise "getShortMatrixAsList already defined!"
end
getShortMatrixAsList = callback("getShortMatrixAsList")
getShortMatrixAsListType = getShortMatrixAsList[0][0].class
puts(getShortMatrixAsListType)
puts(getShortMatrixAsList)
if(getShortMatrixAsListType != Integer)
	raise "getShortMatrixAsList not Integer!"
end
callback("setShortMatrixAsList", getShortMatrixAsList)

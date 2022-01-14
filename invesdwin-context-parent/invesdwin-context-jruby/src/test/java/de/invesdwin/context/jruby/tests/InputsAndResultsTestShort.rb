puts("getShort")
unless (defined?(getShort)).nil?
	raise "getShort already defined!"
end
getShort = putShort
getShortType = getShort.class
puts(getShortType)
puts(getShort)
if(getShortType != 'number')
	raise "getShort not number!"
end

puts("getShortVector")
unless (defined?(getShortVector)).nil?
	raise "getShortVector already defined!"
end
getShortVector = putShortVector
getShortVectorType = getShortVector[0].class
puts(getShortVectorType)
puts(getShortVector)
if(getShortVectorType != 'number')
	raise "getShortVector not number!"
end

puts("getShortVectorAsList")
unless (defined?(getShortVectorAsList)).nil?
	raise "getShortVectorAsList already defined!"
end
getShortVectorAsList = putShortVectorAsList
getShortVectorAsListType = getShortVectorAsList[0].class
puts(getShortVectorAsListType)
puts(getShortVectorAsList)
if(getShortVectorAsListType != 'number')
	raise "getShortVectorAsList not number!"
end

puts("getShortMatrix")
unless (defined?(getShortMatrix)).nil?
	raise "getShortMatrix already defined!"
end
getShortMatrix = putShortMatrix
getShortMatrixType = getShortMatrix[0][0].class
puts(getShortMatrixType)
puts(getShortMatrix)
if(getShortMatrixType != 'number')
	raise "getShortMatrix not number!"
end

puts("getShortMatrixAsList")
unless (defined?(getShortMatrixAsList)).nil?
	raise "getShortMatrixAsList already defined!"
end
getShortMatrixAsList = putShortMatrixAsList
getShortMatrixAsListType = getShortMatrixAsList[0][0].class
puts(getShortMatrixAsListType)
puts(getShortMatrixAsList)
if(getShortMatrixAsListType != 'number')
	raise "getShortMatrixAsList not number!"
end

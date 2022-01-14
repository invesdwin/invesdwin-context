puts("getByte")
unless (defined?(getByte)).nil?
	raise "getByte already defined" + (defined?(getByte)).nil?
end
getByte = putByte
getByteType = getByte.class
puts(getByteType)
puts(getByte)
if(getByteType != 'number')
	raise "getByte not number!"
end

puts("getByteVector")
unless (defined?(getByteVector)).nil?
	raise "getByteVector already defined"
end
getByteVector = putByteVector
getByteVectorType = getByteVector[0].class
puts(getByteVectorType)
puts(getByteVector)
if(getByteVectorType != 'number')
	raise "getByteVector not number!"
end

puts("getByteVectorAsList")
unless (defined?(getByteVectorAsList)).nil?
	raise "getByteVectorAsList already defined"
end
getByteVectorAsList = putByteVectorAsList
getByteVectorAsListType = getByteVectorAsList[0].class
puts(getByteVectorAsListType)
puts(getByteVectorAsList)
if(getByteVectorAsListType != 'number')
	raise "getByteVectorAsList not number!"
end

puts("getByteMatrix")
unless (defined?(getByteMatrix)).nil?
	raise "getByteMatrix already defined"
end
getByteMatrix = putByteMatrix
getByteMatrixType = getByteMatrix[0][0].class
puts(getByteMatrixType)
puts(getByteMatrix)
if(getByteMatrixType != 'number')
	raise "getByteMatrix not number!"
end

puts("getByteMatrixAsList")
unless (defined?(getByteMatrixAsList)).nil?
	raise "getByteMatrixAsList already defined"
end
getByteMatrixAsList = putByteMatrixAsList
getByteMatrixAsListType = getByteMatrixAsList[0][0].class
puts(getByteMatrixAsListType)
puts(getByteMatrixAsList)
if(getByteMatrixAsListType != 'number')
	raise "getByteMatrixAsList not number!"
end

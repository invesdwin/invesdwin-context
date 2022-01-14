puts("getByte")
unless (defined?(getByte)).nil?
	raise "getByte already defined" + (defined?(getByte)).nil?
end
getByte = putByte
getByteType = getByte.class
puts(getByteType)
puts(getByte)
if(getByteType != Integer)
	raise "getByte not Integer!"
end

puts("getByteVector")
unless (defined?(getByteVector)).nil?
	raise "getByteVector already defined"
end
getByteVector = putByteVector
getByteVectorType = getByteVector[0].class
puts(getByteVectorType)
puts(getByteVector)
if(getByteVectorType != Integer)
	raise "getByteVector not Integer!"
end

puts("getByteVectorAsList")
unless (defined?(getByteVectorAsList)).nil?
	raise "getByteVectorAsList already defined"
end
getByteVectorAsList = putByteVectorAsList
getByteVectorAsListType = getByteVectorAsList[0].class
puts(getByteVectorAsListType)
puts(getByteVectorAsList)
if(getByteVectorAsListType != Integer)
	raise "getByteVectorAsList not Integer!"
end

puts("getByteMatrix")
unless (defined?(getByteMatrix)).nil?
	raise "getByteMatrix already defined"
end
getByteMatrix = putByteMatrix
getByteMatrixType = getByteMatrix[0][0].class
puts(getByteMatrixType)
puts(getByteMatrix)
if(getByteMatrixType != Integer)
	raise "getByteMatrix not Integer!"
end

puts("getByteMatrixAsList")
unless (defined?(getByteMatrixAsList)).nil?
	raise "getByteMatrixAsList already defined"
end
getByteMatrixAsList = putByteMatrixAsList
getByteMatrixAsListType = getByteMatrixAsList[0][0].class
puts(getByteMatrixAsListType)
puts(getByteMatrixAsList)
if(getByteMatrixAsListType != Integer)
	raise "getByteMatrixAsList not Integer!"
end

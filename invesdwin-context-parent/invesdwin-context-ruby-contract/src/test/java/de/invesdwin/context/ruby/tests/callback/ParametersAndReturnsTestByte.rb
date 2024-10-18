puts("getByte")
unless (defined?(getByte)).nil? || getByte.nil?
	raise "getByte already defined"
end
getByte = callback("getByte")
getByteType = getByte.class
puts(getByteType)
puts(getByte)
if(getByteType != Integer)
	raise "getByte not Integer!"
end
callback("setByte", getByte)

puts("getByteVector")
unless (defined?(getByteVector)).nil? || getByteVector.nil?
	raise "getByteVector already defined"
end
getByteVector = callback("getByteVector")
getByteVectorType = getByteVector[0].class
puts(getByteVectorType)
puts(getByteVector)
if(getByteVectorType != Integer)
	raise "getByteVector not Integer!"
end
callback("setByteVector", getByteVector)

puts("getByteVectorAsList")
unless (defined?(getByteVectorAsList)).nil? || getByteVectorAsList.nil?
	raise "getByteVectorAsList already defined"
end
getByteVectorAsList = callback("getByteVectorAsList")
getByteVectorAsListType = getByteVectorAsList[0].class
puts(getByteVectorAsListType)
puts(getByteVectorAsList)
if(getByteVectorAsListType != Integer)
	raise "getByteVectorAsList not Integer!"
end
callback("setByteVectorAsList", getByteVectorAsList)

puts("getByteMatrix")
unless (defined?(getByteMatrix)).nil? || getByteMatrix.nil?
	raise "getByteMatrix already defined"
end
getByteMatrix = callback("getByteMatrix")
getByteMatrixType = getByteMatrix[0][0].class
puts(getByteMatrixType)
puts(getByteMatrix)
if(getByteMatrixType != Integer)
	raise "getByteMatrix not Integer!"
end
callback("setByteMatrix", getByteMatrix)

puts("getByteMatrixAsList")
unless (defined?(getByteMatrixAsList)).nil? || getByteMatrixAsList.nil?
	raise "getByteMatrixAsList already defined"
end
getByteMatrixAsList = callback("getByteMatrixAsList")
getByteMatrixAsListType = getByteMatrixAsList[0][0].class
puts(getByteMatrixAsListType)
puts(getByteMatrixAsList)
if(getByteMatrixAsListType != Integer)
	raise "getByteMatrixAsList not Integer!"
end
callback("setByteMatrixAsList", getByteMatrixAsList)

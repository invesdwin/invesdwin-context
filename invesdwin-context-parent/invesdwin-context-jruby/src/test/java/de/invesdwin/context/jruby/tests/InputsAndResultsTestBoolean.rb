puts("getBoolean")
if (defined?(getBoolean)).nil?
	raise "getBoolean already defined!"
end
getBoolean = putBoolean
getBooleanType = getBoolean.class
puts(getBooleanType)
puts(getBoolean)
if(getBooleanType != 'boolean')
	raise "getBoolean not boolean!"
end

puts("getBooleanVector")
if (defined?(getBooleanVector)).nil?
	raise "getBooleanVector already defined!"
end
getBooleanVector = putBooleanVector
getBooleanVectorType = getBooleanVector[0].class
puts(getBooleanVectorType)
puts(getBooleanVector)
if(getBooleanVectorType != 'boolean')
	raise "getBooleanVector not boolean!"
end

puts("getBooleanVectorAsList")
if (defined?(getBooleanVectorAsList)).nil?
	raise "getBooleanVectorAsList already defined!"
end
getBooleanVectorAsList = putBooleanVectorAsList
getBooleanVectorAsListType = getBooleanVectorAsList[0].class
puts(getBooleanVectorAsListType)
puts(getBooleanVectorAsList)
if(getBooleanVectorAsListType != 'boolean')
	raise "getBooleanVectorAsList not boolean!"
end

puts("getBooleanMatrix")
if (defined?(getBooleanMatrix)).nil?
	raise "getBooleanMatrix already defined!"
end
getBooleanMatrix = putBooleanMatrix
getBooleanMatrixType = getBooleanMatrix[0][0].class
puts(getBooleanMatrixType)
puts(getBooleanMatrix)
if(getBooleanMatrixType != 'boolean')
	raise "getBooleanMatrix not boolean!"
end

puts("getBooleanMatrixAsList")
if (defined?(getBooleanMatrixAsList)).nil?
	raise "getBooleanMatrixAsList already defined!"
end
getBooleanMatrixAsList = putBooleanMatrixAsList
getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].class
puts(getBooleanMatrixAsListType)
puts(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != 'boolean')
	raise "getBooleanMatrixAsList not boolean!"
end

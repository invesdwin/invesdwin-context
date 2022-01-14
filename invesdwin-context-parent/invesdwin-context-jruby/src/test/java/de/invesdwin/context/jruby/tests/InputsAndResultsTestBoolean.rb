puts("getBoolean")
unless (defined?(getBoolean)).nil?
	raise "getBoolean already defined!"
end
getBoolean = putBoolean
getBooleanType = getBoolean.class
puts(getBooleanType)
puts(getBoolean)
if(getBooleanType != TrueClass and getBooleanType != FalseClass)
	raise "getBoolean not boolean!"
end

puts("getBooleanVector")
unless (defined?(getBooleanVector)).nil?
	raise "getBooleanVector already defined!"
end
getBooleanVector = putBooleanVector
getBooleanVectorType = getBooleanVector[0].class
puts(getBooleanVectorType)
puts(getBooleanVector)
if(getBooleanVectorType != TrueClass and getBooleanVectorType != FalseClass)
	raise "getBooleanVector not boolean!"
end

puts("getBooleanVectorAsList")
unless (defined?(getBooleanVectorAsList)).nil?
	raise "getBooleanVectorAsList already defined!"
end
getBooleanVectorAsList = putBooleanVectorAsList
getBooleanVectorAsListType = getBooleanVectorAsList[0].class
puts(getBooleanVectorAsListType)
puts(getBooleanVectorAsList)
if(getBooleanVectorAsListType != TrueClass and getBooleanVectorAsListType != FalseClass)
	raise "getBooleanVectorAsList not boolean!"
end

puts("getBooleanMatrix")
unless (defined?(getBooleanMatrix)).nil?
	raise "getBooleanMatrix already defined!"
end
getBooleanMatrix = putBooleanMatrix
getBooleanMatrixType = getBooleanMatrix[0][0].class
puts(getBooleanMatrixType)
puts(getBooleanMatrix)
if(getBooleanMatrixType != TrueClass and getBooleanMatrixType != FalseClass)
	raise "getBooleanMatrix not boolean!"
end

puts("getBooleanMatrixAsList")
unless (defined?(getBooleanMatrixAsList)).nil?
	raise "getBooleanMatrixAsList already defined!"
end
getBooleanMatrixAsList = putBooleanMatrixAsList
getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].class
puts(getBooleanMatrixAsListType)
puts(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != TrueClass and getBooleanMatrixAsListType != FalseClass)
	raise "getBooleanMatrixAsList not boolean!"
end

puts("getBoolean")
unless (defined?(getBoolean)).nil? || getBoolean.nil?
	raise "getBoolean already defined!"
end
getBoolean = callback("getBoolean")
getBooleanType = getBoolean.class
puts(getBooleanType)
puts(getBoolean)
if(getBooleanType != TrueClass and getBooleanType != FalseClass)
	raise "getBoolean not boolean!"
end
callback("setBoolean", getBoolean)

puts("getBooleanVector")
unless (defined?(getBooleanVector)).nil? || getBooleanVector.nil?
	raise "getBooleanVector already defined!"
end
getBooleanVector = callback("getBooleanVector")
getBooleanVectorType = getBooleanVector[0].class
puts(getBooleanVectorType)
puts(getBooleanVector)
if(getBooleanVectorType != TrueClass and getBooleanVectorType != FalseClass)
	raise "getBooleanVector not boolean!"
end
callback("setBooleanVector", getBooleanVector)

puts("getBooleanVectorAsList")
unless (defined?(getBooleanVectorAsList)).nil? || getBooleanVectorAsList.nil?
	raise "getBooleanVectorAsList already defined!"
end
getBooleanVectorAsList = callback("getBooleanVectorAsList")
getBooleanVectorAsListType = getBooleanVectorAsList[0].class
puts(getBooleanVectorAsListType)
puts(getBooleanVectorAsList)
if(getBooleanVectorAsListType != TrueClass and getBooleanVectorAsListType != FalseClass)
	raise "getBooleanVectorAsList not boolean!"
end
callback("setBooleanVectorAsList", getBooleanVectorAsList)

puts("getBooleanMatrix")
unless (defined?(getBooleanMatrix)).nil? || getBooleanMatrix.nil?
	raise "getBooleanMatrix already defined!"
end
getBooleanMatrix = callback("getBooleanMatrix")
getBooleanMatrixType = getBooleanMatrix[0][0].class
puts(getBooleanMatrixType)
puts(getBooleanMatrix)
if(getBooleanMatrixType != TrueClass and getBooleanMatrixType != FalseClass)
	raise "getBooleanMatrix not boolean!"
end
callback("setBooleanMatrix", getBooleanMatrix)

puts("getBooleanMatrixAsList")
unless (defined?(getBooleanMatrixAsList)).nil? || getBooleanMatrixAsList.nil?
	raise "getBooleanMatrixAsList already defined!"
end
getBooleanMatrixAsList = callback("getBooleanMatrixAsList")
getBooleanMatrixAsListType = getBooleanMatrixAsList[0][0].class
puts(getBooleanMatrixAsListType)
puts(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != TrueClass and getBooleanMatrixAsListType != FalseClass)
	raise "getBooleanMatrixAsList not boolean!"
end
callback("setBooleanMatrixAsList", getBooleanMatrixAsList)

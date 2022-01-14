puts("getLong")
if (defined?(getLong)).nil?
	raise "getLong already defined!"
end
getLong = putLong
getLongType = getLong.class
puts(getLongType)
puts(getLong)
if(getLongType != 'number')
	raise "getLong not number!"
end

puts("getLongVector")
if (defined?(getLongVector)).nil?
	raise "getLongVector already defined!"
end
getLongVector = putLongVector
getLongVectorType = getLongVector[0].class
puts(getLongVectorType)
puts(getLongVector)
if(getLongVectorType != 'number')
	raise "getLongVector not number!"
end

puts("getLongVectorAsList")
if (defined?(getLongVectorAsList)).nil?
	raise "getLongVectorAsList already defined!"
end
getLongVectorAsList = putLongVectorAsList
getLongVectorAsListType = getLongVectorAsList[0].class
puts(getLongVectorAsListType)
puts(getLongVectorAsList)
if(getLongVectorAsListType != 'number')
	raise "getLongVectorAsList not number!"
end

puts("getLongMatrix")
if (defined?(getLongMatrix)).nil?
	raise "getLongMatrix already defined!"
end
getLongMatrix = putLongMatrix
getLongMatrixType = getLongMatrix[0][0].class
puts(getLongMatrixType)
puts(getLongMatrix)
if(getLongMatrixType != 'number')
	raise "getLongMatrix not number!"
end

puts("getLongMatrixAsList")
if (defined?(getLongMatrixAsList)).nil?
	raise "getLongMatrixAsList already defined!"
end
getLongMatrixAsList = putLongMatrixAsList
getLongMatrixAsListType = getLongMatrixAsList[0][0].class
puts(getLongMatrixAsListType)
puts(getLongMatrixAsList)
if(getLongMatrixAsListType != 'number')
	raise "getLongMatrixAsList not number!"
end

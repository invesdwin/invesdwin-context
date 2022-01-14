puts("getLong")
unless (defined?(getLong)).nil?
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
unless (defined?(getLongVector)).nil?
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
unless (defined?(getLongVectorAsList)).nil?
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
unless (defined?(getLongMatrix)).nil?
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
unless (defined?(getLongMatrixAsList)).nil?
	raise "getLongMatrixAsList already defined!"
end
getLongMatrixAsList = putLongMatrixAsList
getLongMatrixAsListType = getLongMatrixAsList[0][0].class
puts(getLongMatrixAsListType)
puts(getLongMatrixAsList)
if(getLongMatrixAsListType != 'number')
	raise "getLongMatrixAsList not number!"
end

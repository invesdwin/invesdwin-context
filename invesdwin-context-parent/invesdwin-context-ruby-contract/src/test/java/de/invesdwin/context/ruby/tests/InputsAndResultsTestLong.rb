puts("getLong")
unless (defined?(getLong)).nil? || getLong.nil?
	raise "getLong already defined!"
end
getLong = putLong
getLongType = getLong.class
puts(getLongType)
puts(getLong)
if(getLongType != Integer)
	raise "getLong not Integer!"
end

puts("getLongVector")
unless (defined?(getLongVector)).nil? || getLongVector.nil?
	raise "getLongVector already defined!"
end
getLongVector = putLongVector
getLongVectorType = getLongVector[0].class
puts(getLongVectorType)
puts(getLongVector)
if(getLongVectorType != Integer)
	raise "getLongVector not Integer!"
end

puts("getLongVectorAsList")
unless (defined?(getLongVectorAsList)).nil? || getLongVectorAsList.nil?
	raise "getLongVectorAsList already defined!"
end
getLongVectorAsList = putLongVectorAsList
getLongVectorAsListType = getLongVectorAsList[0].class
puts(getLongVectorAsListType)
puts(getLongVectorAsList)
if(getLongVectorAsListType != Integer)
	raise "getLongVectorAsList not Integer!"
end

puts("getLongMatrix")
unless (defined?(getLongMatrix)).nil? || getLongMatrix.nil?
	raise "getLongMatrix already defined!"
end
getLongMatrix = putLongMatrix
getLongMatrixType = getLongMatrix[0][0].class
puts(getLongMatrixType)
puts(getLongMatrix)
if(getLongMatrixType != Integer)
	raise "getLongMatrix not Integer!"
end

puts("getLongMatrixAsList")
unless (defined?(getLongMatrixAsList)).nil? || getLongMatrixAsList.nil?
	raise "getLongMatrixAsList already defined!"
end
getLongMatrixAsList = putLongMatrixAsList
getLongMatrixAsListType = getLongMatrixAsList[0][0].class
puts(getLongMatrixAsListType)
puts(getLongMatrixAsList)
if(getLongMatrixAsListType != Integer)
	raise "getLongMatrixAsList not Integer!"
end

puts("getLong")
unless (defined?(getLong)).nil?
	raise "getLong already defined!"
end
getLong = callback("getLong")
getLongType = getLong.class
puts(getLongType)
puts(getLong)
if(getLongType != Integer)
	raise "getLong not Integer!"
end
callback("setLong", getLong)

puts("getLongVector")
unless (defined?(getLongVector)).nil?
	raise "getLongVector already defined!"
end
getLongVector = callback("getLongVector")
getLongVectorType = getLongVector[0].class
puts(getLongVectorType)
puts(getLongVector)
if(getLongVectorType != Integer)
	raise "getLongVector not Integer!"
end
callback("setLongVector", getLongVector)

puts("getLongVectorAsList")
unless (defined?(getLongVectorAsList)).nil?
	raise "getLongVectorAsList already defined!"
end
getLongVectorAsList = callback("getLongVectorAsList")
getLongVectorAsListType = getLongVectorAsList[0].class
puts(getLongVectorAsListType)
puts(getLongVectorAsList)
if(getLongVectorAsListType != Integer)
	raise "getLongVectorAsList not Integer!"
end
callback("setLongVectorAsList", getLongVectorAsList)

puts("getLongMatrix")
unless (defined?(getLongMatrix)).nil?
	raise "getLongMatrix already defined!"
end
getLongMatrix = callback("getLongMatrix")
getLongMatrixType = getLongMatrix[0][0].class
puts(getLongMatrixType)
puts(getLongMatrix)
if(getLongMatrixType != Integer)
	raise "getLongMatrix not Integer!"
end
callback("setLongMatrix", getLongMatrix)

puts("getLongMatrixAsList")
unless (defined?(getLongMatrixAsList)).nil?
	raise "getLongMatrixAsList already defined!"
end
getLongMatrixAsList = callback("getLongMatrixAsList")
getLongMatrixAsListType = getLongMatrixAsList[0][0].class
puts(getLongMatrixAsListType)
puts(getLongMatrixAsList)
if(getLongMatrixAsListType != Integer)
	raise "getLongMatrixAsList not Integer!"
end
callback("setLongMatrixAsList", getLongMatrixAsList)

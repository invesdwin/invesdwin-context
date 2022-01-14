puts("getPercent")
unless (defined?(getPercent)).nil?
	raise "getPercent already defined!"
end
getPercent = putPercent
getPercentType = getPercent.class
puts(getPercentType)
puts(getPercent)
if(getPercentType != 'number')
	raise "getPercent not number!"
end

puts("getPercentVector")
unless (defined?(getPercentVector)).nil?
	raise "getPercentVector already defined!"
end
getPercentVector = putPercentVector
getPercentVectorType = getPercentVector[0].class
puts(getPercentVectorType)
puts(getPercentVector)
if(getPercentVectorType != 'number')
	raise "getPercentVector not number!"
end

puts("getPercentVectorAsList")
unless (defined?(getPercentVectorAsList)).nil?
	raise "getPercentVectorAsList already defined!"
end
getPercentVectorAsList = putPercentVectorAsList
getPercentVectorAsListType = getPercentVectorAsList[0].class
puts(getPercentVectorAsListType)
puts(getPercentVectorAsList)
if(getPercentVectorAsListType != 'number')
	raise "getPercentVectorAsList not number!"
end

puts("getPercentMatrix")
unless (defined?(getPercentMatrix)).nil?
	raise "getPercentMatrix already defined!"
end
getPercentMatrix = putPercentMatrix
getPercentMatrixType = getPercentMatrix[0][0].class
puts(getPercentMatrixType)
puts(getPercentMatrix)
if(getPercentMatrixType != 'number')
	raise "getPercentMatrix not number!"
end

puts("getPercentMatrixAsList")
unless (defined?(getPercentMatrixAsList)).nil?
	raise "getPercentMatrixAsList already defined!"
end
getPercentMatrixAsList = putPercentMatrixAsList
getPercentMatrixAsListType = getPercentMatrixAsList[0][0].class
puts(getPercentMatrixAsListType)
puts(getPercentMatrixAsList)
if(getPercentMatrixAsListType != 'number')
	raise "getPercentMatrixAsList not number!"
end

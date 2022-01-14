puts("getPercent")
unless (defined?(getPercent)).nil?
	raise "getPercent already defined!"
end
getPercent = putPercent
getPercentType = getPercent.class
puts(getPercentType)
puts(getPercent)
if(getPercentType != Float)
	raise "getPercent not Float!"
end

puts("getPercentVector")
unless (defined?(getPercentVector)).nil?
	raise "getPercentVector already defined!"
end
getPercentVector = putPercentVector
getPercentVectorType = getPercentVector[0].class
puts(getPercentVectorType)
puts(getPercentVector)
if(getPercentVectorType != Float)
	raise "getPercentVector not Float!"
end

puts("getPercentVectorAsList")
unless (defined?(getPercentVectorAsList)).nil?
	raise "getPercentVectorAsList already defined!"
end
getPercentVectorAsList = putPercentVectorAsList
getPercentVectorAsListType = getPercentVectorAsList[0].class
puts(getPercentVectorAsListType)
puts(getPercentVectorAsList)
if(getPercentVectorAsListType != Float)
	raise "getPercentVectorAsList not Float!"
end

puts("getPercentMatrix")
unless (defined?(getPercentMatrix)).nil?
	raise "getPercentMatrix already defined!"
end
getPercentMatrix = putPercentMatrix
getPercentMatrixType = getPercentMatrix[0][0].class
puts(getPercentMatrixType)
puts(getPercentMatrix)
if(getPercentMatrixType != Float)
	raise "getPercentMatrix not Float!"
end

puts("getPercentMatrixAsList")
unless (defined?(getPercentMatrixAsList)).nil?
	raise "getPercentMatrixAsList already defined!"
end
getPercentMatrixAsList = putPercentMatrixAsList
getPercentMatrixAsListType = getPercentMatrixAsList[0][0].class
puts(getPercentMatrixAsListType)
puts(getPercentMatrixAsList)
if(getPercentMatrixAsListType != Float)
	raise "getPercentMatrixAsList not Float!"
end

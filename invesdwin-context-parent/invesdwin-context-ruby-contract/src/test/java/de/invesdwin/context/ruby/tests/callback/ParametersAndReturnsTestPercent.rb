puts("getPercent")
unless (defined?(getPercent)).nil? || getPercent.nil?
	raise "getPercent already defined!"
end
getPercent = callback("getPercent")
getPercentType = getPercent.class
puts(getPercentType)
puts(getPercent)
if(getPercentType != Float)
	raise "getPercent not Float!"
end
callback("setPercent", getPercent)

puts("getPercentVector")
unless (defined?(getPercentVector)).nil? || getPercentVector.nil?
	raise "getPercentVector already defined!"
end
getPercentVector = callback("getPercentVector")
getPercentVectorType = getPercentVector[0].class
puts(getPercentVectorType)
puts(getPercentVector)
if(getPercentVectorType != Float)
	raise "getPercentVector not Float!"
end
callback("setPercentVector", getPercentVector)

puts("getPercentVectorAsList")
unless (defined?(getPercentVectorAsList)).nil? || getPercentVectorAsList.nil?
	raise "getPercentVectorAsList already defined!"
end
getPercentVectorAsList = callback("getPercentVectorAsList")
getPercentVectorAsListType = getPercentVectorAsList[0].class
puts(getPercentVectorAsListType)
puts(getPercentVectorAsList)
if(getPercentVectorAsListType != Float)
	raise "getPercentVectorAsList not Float!"
end
callback("setPercentVectorAsList", getPercentVectorAsList)

puts("getPercentMatrix")
unless (defined?(getPercentMatrix)).nil? || getPercentMatrix.nil?
	raise "getPercentMatrix already defined!"
end
getPercentMatrix = callback("getPercentMatrix")
getPercentMatrixType = getPercentMatrix[0][0].class
puts(getPercentMatrixType)
puts(getPercentMatrix)
if(getPercentMatrixType != Float)
	raise "getPercentMatrix not Float!"
end
callback("setPercentMatrix", getPercentMatrix)

puts("getPercentMatrixAsList")
unless (defined?(getPercentMatrixAsList)).nil? || getPercentMatrixAsList.nil?
	raise "getPercentMatrixAsList already defined!"
end
getPercentMatrixAsList = callback("getPercentMatrixAsList")
getPercentMatrixAsListType = getPercentMatrixAsList[0][0].class
puts(getPercentMatrixAsListType)
puts(getPercentMatrixAsList)
if(getPercentMatrixAsListType != Float)
	raise "getPercentMatrixAsList not Float!"
end
callback("setPercentMatrixAsList", getPercentMatrixAsList)

puts("getDouble")
unless (defined?(getDouble)).nil? || getDouble.nil?
	raise "getDouble already defined!"
end
getDouble = callback("getDouble")
getDoubleType = getDouble.class
puts(getDoubleType)
puts(getDouble)
if(getDoubleType != Float)
	raise "getDouble not Float!"
end
if(!getDouble.nan?)
	raise "getDouble not NaN!"
end
callback("setDouble", getDouble)

puts("getDoubleVector")
unless (defined?(getDoubleVector)).nil? || getDoubleVector.nil?
	raise "getDoubleVector already defined!"
end
getDoubleVector = callback("getDoubleVector")
getDoubleVectorType = getDoubleVector[0].class
puts(getDoubleVectorType)
puts(getDoubleVector)
if(getDoubleVectorType != Float)
	raise "getDoubleVector not Float!"
end
if(!getDoubleVector[1].nan?)
	raise "getDoubleVector[1] not NaN!"
end
callback("setDoubleVector", getDoubleVector)

puts("getDoubleVectorAsList")
unless (defined?(getDoubleVectorAsList)).nil? || getDoubleVectorAsList.nil?
	raise "getDoubleVectorAsList already defined!"
end
getDoubleVectorAsList = callback("getDoubleVectorAsList")
getDoubleVectorAsListType = getDoubleVectorAsList[0].class
puts(getDoubleVectorAsListType)
puts(getDoubleVectorAsList)
if(getDoubleVectorAsListType != Float)
	raise "getDoubleVectorAsList not Float!"
end
if(!getDoubleVectorAsList[1].nan?)
	raise "getDoubleVectorAsList[1] not NaN!"
end
callback("setDoubleVectorAsList", getDoubleVectorAsList)

puts("getDoubleMatrix")
unless (defined?(getDoubleMatrix)).nil? || getDoubleMatrix.nil?
	raise "getDoubleMatrix already defined!"
end
getDoubleMatrix = callback("getDoubleMatrix")
getDoubleMatrixType = getDoubleMatrix[0][0].class
puts(getDoubleMatrixType)
puts(getDoubleMatrix)
if(getDoubleMatrixType != Float)
	raise "getDoubleMatrix not Float!"
end
if(!getDoubleMatrix[0][0].nan?)
	raise "getDoubleMatrix[0][0] not NaN!"
end
if(!getDoubleMatrix[1][1].nan?)
	raise "getDoubleMatrix[1][1] not NaN!"
end
if(!getDoubleMatrix[2][2].nan?)
	raise "getDoubleMatrix[2][2] not NaN!"
end
callback("setDoubleMatrix", getDoubleMatrix)

puts("getDoubleMatrixAsList")
unless (defined?(getDoubleMatrixAsList)).nil? || getDoubleMatrixAsList.nil?
	raise "getDoubleMatrixAsList already defined!"
end
getDoubleMatrixAsList = callback("getDoubleMatrixAsList")
getDoubleMatrixAsListType = getDoubleMatrixAsList[0][0].class
puts(getDoubleMatrixAsListType)
puts(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != Float)
	raise "getDoubleMatrixAsList not Float!"
end
if(!getDoubleMatrixAsList[0][0].nan?)
	raise "getDoubleMatrixAsList[0][0] not NaN!"
end
if(!getDoubleMatrixAsList[1][1].nan?)
	raise "getDoubleMatrixAsList[1][1] not NaN!"
end
if(!getDoubleMatrixAsList[2][2].nan?)
	raise "getDoubleMatrixAsList[2][2] not NaN!"
end
callback("setDoubleMatrixAsList", getDoubleMatrixAsList)

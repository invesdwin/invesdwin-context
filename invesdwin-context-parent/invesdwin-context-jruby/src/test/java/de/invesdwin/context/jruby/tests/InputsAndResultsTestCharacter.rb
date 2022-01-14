puts("getCharacter")
unless (defined?(getCharacter)).nil?
	raise "getCharacter already defined!"
end
getCharacter = putCharacter
getCharacterType = getCharacter.class
puts(getCharacterType)
puts(getCharacter)
if(getCharacterType != Integer)
	raise "getCharacter not Integer"
end

puts("getCharacterVector")
unless (defined?(getCharacterVector)).nil?
	raise "getCharacterVector already defined!"
end
getCharacterVector = putCharacterVector
getCharacterVectorType = getCharacterVector[0].class
puts(getCharacterVectorType)
puts(getCharacterVector)
if(getCharacterVectorType != Integer)
	raise "getCharacterVector not Integer"
end

puts("getCharacterVectorAsList")
unless (defined?(getCharacterVectorAsList)).nil?
	raise "getCharacterVectorAsList already defined!"
end
getCharacterVectorAsList = putCharacterVectorAsList
getCharacterVectorAsListType = getCharacterVectorAsList[0].class
puts(getCharacterVectorAsListType)
puts(getCharacterVectorAsList)
if(getCharacterVectorAsListType != Integer)
	raise "getCharacterVectorAsList not Integer"
end

puts("getCharacterMatrix")
unless (defined?(getCharacterMatrix)).nil?
	raise "getCharacterMatrix already defined!"
end
getCharacterMatrix = putCharacterMatrix
getCharacterMatrixType = getCharacterMatrix[0][0].class
puts(getCharacterMatrixType)
puts(getCharacterMatrix)
if(getCharacterMatrixType != Integer)
	raise "getCharacterMatrix not Integer"
end

puts("getCharacterMatrixAsList")
unless (defined?(getCharacterMatrixAsList)).nil?
	raise "getCharacterMatrixAsList already defined!"
end
getCharacterMatrixAsList = putCharacterMatrixAsList
getCharacterMatrixAsListType = getCharacterMatrixAsList[0][0].class
puts(getCharacterMatrixAsListType)
puts(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != Integer)
	raise "getCharacterMatrixAsList not Integer"
end
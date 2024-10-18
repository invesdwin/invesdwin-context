puts("getCharacter")
unless (defined?(getCharacter)).nil? || getCharacter.nil?
	raise "getCharacter already defined!"
end
getCharacter = callback("getCharacter")
getCharacterType = getCharacter.class
puts(getCharacterType)
puts(getCharacter)
if(getCharacterType != Integer)
	raise "getCharacter not Integer"
end
callback("setCharacter", getCharacter)

puts("getCharacterVector")
unless (defined?(getCharacterVector)).nil? || getCharacterVector.nil?
	raise "getCharacterVector already defined!"
end
getCharacterVector = callback("getCharacterVector")
getCharacterVectorType = getCharacterVector[0].class
puts(getCharacterVectorType)
puts(getCharacterVector)
if(getCharacterVectorType != Integer)
	raise "getCharacterVector not Integer"
end
callback("setCharacterVector", getCharacterVector)

puts("getCharacterVectorAsList")
unless (defined?(getCharacterVectorAsList)).nil? || getCharacterVectorAsList.nil?
	raise "getCharacterVectorAsList already defined!"
end
getCharacterVectorAsList = callback("getCharacterVectorAsList")
getCharacterVectorAsListType = getCharacterVectorAsList[0].class
puts(getCharacterVectorAsListType)
puts(getCharacterVectorAsList)
if(getCharacterVectorAsListType != Integer)
	raise "getCharacterVectorAsList not Integer"
end
callback("setCharacterVectorAsList", getCharacterVectorAsList)

puts("getCharacterMatrix")
unless (defined?(getCharacterMatrix)).nil? || getCharacterMatrix.nil?
	raise "getCharacterMatrix already defined!"
end
getCharacterMatrix = callback("getCharacterMatrix")
getCharacterMatrixType = getCharacterMatrix[0][0].class
puts(getCharacterMatrixType)
puts(getCharacterMatrix)
if(getCharacterMatrixType != Integer)
	raise "getCharacterMatrix not Integer"
end
callback("setCharacterMatrix", getCharacterMatrix)

puts("getCharacterMatrixAsList")
unless (defined?(getCharacterMatrixAsList)).nil? || getCharacterMatrixAsList.nil?
	raise "getCharacterMatrixAsList already defined!"
end
getCharacterMatrixAsList = callback("getCharacterMatrixAsList")
getCharacterMatrixAsListType = getCharacterMatrixAsList[0][0].class
puts(getCharacterMatrixAsListType)
puts(getCharacterMatrixAsList)
if(getCharacterMatrixAsListType != Integer)
	raise "getCharacterMatrixAsList not Integer"
end
callback("setCharacterMatrixAsList", getCharacterMatrixAsList)

System.out.println("getShort")
if(getShort != null)
	throw new Exception("getShort already defined!")
val getShort: java.lang.Short = callback("getShort")
val getShortType = getShort.getClass()
System.out.println(getShortType)
System.out.println(getShort)
if(getShortType != classOf[java.lang.Short])
	throw new Exception("getShort not Short!")
callback("setShort",getShort)

System.out.println("getShortVector")
if(getShortVector != null)
	throw new Exception("getShortVector already defined!")
val getShortVector: Array[Short] = callback("getShortVector")
val getShortVectorType = getShortVector(0).getClass()
System.out.println(getShortVectorType)
System.out.println(getShortVector)
if(getShortVectorType != classOf[Short])
	throw new Exception("getShortVector not Short!")
callback("setShortVector",getShortVector)

System.out.println("getShortVectorAsList")
if(getShortVectorAsList != null)
	throw new Exception("getShortVectorAsList already defined!")
val getShortVectorAsList: Array[Short] = callback("getShortVectorAsList")
val getShortVectorAsListType = getShortVectorAsList(0).getClass()
System.out.println(getShortVectorAsListType)
System.out.println(getShortVectorAsList)
if(getShortVectorAsListType != classOf[Short])
	throw new Exception("getShortVectorAsList not Short!")
callback("setShortVectorAsList",getShortVectorAsList)

System.out.println("getShortMatrix")
if(getShortMatrix != null)
	throw new Exception("getShortMatrix already defined!")
val getShortMatrix: Array[Array[Short]] = callback("getShortMatrix")
val getShortMatrixType = getShortMatrix(0)(0).getClass()
System.out.println(getShortMatrixType)
System.out.println(getShortMatrix)
if(getShortMatrixType != classOf[Short])
	throw new Exception("getShortMatrix not Short!")
callback("setShortMatrix",getShortMatrix)

System.out.println("getShortMatrixAsList")
if(getShortMatrixAsList != null)
	throw new Exception("getShortMatrixAsList already defined!")
val getShortMatrixAsList: Array[Array[Short]] = callback("getShortMatrixAsList")
val getShortMatrixAsListType = getShortMatrixAsList(0)(0).getClass()
System.out.println(getShortMatrixAsListType)
System.out.println(getShortMatrixAsList)
if(getShortMatrixAsListType != classOf[Short])
	throw new Exception("getShortMatrixAsList not Short!")
callback("setShortMatrixAsList",getShortMatrixAsList)

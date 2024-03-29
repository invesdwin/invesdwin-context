System.out.println("getBoolean")
if(getBoolean != null)
	throw new Exception("getBoolean already defined!")
val getBoolean: java.lang.Boolean = callback("getBoolean")
val getBooleanType = getBoolean.getClass()
System.out.println(getBooleanType)
System.out.println(getBoolean)
if(getBooleanType != classOf[java.lang.Boolean])
	throw new Exception("getBoolean not Boolean!")
callback("setBoolean",getBoolean)

System.out.println("getBooleanVector")
if(getBooleanVector != null)
	throw new Exception("getBooleanVector already defined!")
val getBooleanVector: Array[Boolean] = callback("getBooleanVector")
val getBooleanVectorType = getBooleanVector(0).getClass()
System.out.println(getBooleanVectorType)
System.out.println(getBooleanVector)
if(getBooleanVectorType != classOf[Boolean])
	throw new Exception("getBooleanVector not Boolean!")
callback("setBooleanVector",getBooleanVector)

System.out.println("getBooleanVectorAsList")
if(getBooleanVectorAsList != null)
	throw new Exception("getBooleanVectorAsList already defined!")
val getBooleanVectorAsList: Array[Boolean] = callback("getBooleanVectorAsList")
val getBooleanVectorAsListType = getBooleanVectorAsList(0).getClass()
System.out.println(getBooleanVectorAsListType)
System.out.println(getBooleanVectorAsList)
if(getBooleanVectorAsListType != classOf[Boolean])
	throw new Exception("getBooleanVectorAsList not Boolean!")
callback("setBooleanVectorAsList",getBooleanVectorAsList)

System.out.println("getBooleanMatrix")
if(getBooleanMatrix != null)
	throw new Exception("getBooleanMatrix already defined!")
val getBooleanMatrix: Array[Array[Boolean]] = callback("getBooleanMatrix")
val getBooleanMatrixType = getBooleanMatrix(0)(0).getClass()
System.out.println(getBooleanMatrixType)
System.out.println(getBooleanMatrix)
if(getBooleanMatrixType != classOf[Boolean])
	throw new Exception("getBooleanMatrix not Boolean!")
callback("setBooleanMatrix",getBooleanMatrix)

System.out.println("getBooleanMatrixAsList")
if(getBooleanMatrixAsList != null)
	throw new Exception("getBooleanMatrixAsList already defined!")
val getBooleanMatrixAsList: Array[Array[Boolean]] = callback("getBooleanMatrixAsList")
val getBooleanMatrixAsListType = getBooleanMatrixAsList(0)(0).getClass()
System.out.println(getBooleanMatrixAsListType)
System.out.println(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != classOf[Boolean])
	throw new Exception("getBooleanMatrixAsList not Boolean!")
callback("setBooleanMatrixAsList",getBooleanMatrixAsList)

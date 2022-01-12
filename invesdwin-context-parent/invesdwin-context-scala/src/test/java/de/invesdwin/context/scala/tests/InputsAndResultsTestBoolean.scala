System.out.println("getBoolean")
if(getBoolean != null)
	throw new Exception("getBoolean already defined!")
val getBoolean = putBoolean
val getBooleanType = getBoolean.getClass()
System.out.println(getBooleanType)
System.out.println(getBoolean)
if(getBooleanType != classOf[java.lang.Boolean])
	throw new Exception("getBoolean not Boolean!")

System.out.println("getBooleanVector")
if(getBooleanVector != null)
	throw new Exception("getBooleanVector already defined!")
val getBooleanVector = putBooleanVector.asInstanceOf[Array[Boolean]]
val getBooleanVectorType = getBooleanVector(0).getClass()
System.out.println(getBooleanVectorType)
System.out.println(getBooleanVector)
if(getBooleanVectorType != classOf[Boolean])
	throw new Exception("getBooleanVector not Boolean!")

System.out.println("getBooleanVectorAsList")
if(getBooleanVectorAsList != null)
	throw new Exception("getBooleanVectorAsList already defined!")
val getBooleanVectorAsList = putBooleanVectorAsList.asInstanceOf[Array[Boolean]]
val getBooleanVectorAsListType = getBooleanVectorAsList(0).getClass()
System.out.println(getBooleanVectorAsListType)
System.out.println(getBooleanVectorAsList)
if(getBooleanVectorAsListType != classOf[Boolean])
	throw new Exception("getBooleanVectorAsList not Boolean!")

System.out.println("getBooleanMatrix")
if(getBooleanMatrix != null)
	throw new Exception("getBooleanMatrix already defined!")
val getBooleanMatrix = putBooleanMatrix.asInstanceOf[Array[Array[Boolean]]]
val getBooleanMatrixType = getBooleanMatrix(0)(0).getClass()
System.out.println(getBooleanMatrixType)
System.out.println(getBooleanMatrix)
if(getBooleanMatrixType != classOf[Boolean])
	throw new Exception("getBooleanMatrix not Boolean!")

System.out.println("getBooleanMatrixAsList")
if(getBooleanMatrixAsList != null)
	throw new Exception("getBooleanMatrixAsList already defined!")
val getBooleanMatrixAsList = putBooleanMatrixAsList.asInstanceOf[Array[Array[Boolean]]]
val getBooleanMatrixAsListType = getBooleanMatrixAsList(0)(0).getClass()
System.out.println(getBooleanMatrixAsListType)
System.out.println(getBooleanMatrixAsList)
if(getBooleanMatrixAsListType != classOf[Boolean])
	throw new Exception("getBooleanMatrixAsList not Boolean!")

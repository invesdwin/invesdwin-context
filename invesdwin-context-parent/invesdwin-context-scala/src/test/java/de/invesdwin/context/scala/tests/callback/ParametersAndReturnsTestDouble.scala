System.out.println("getDouble")
if(getDouble != null)
	throw new Exception("getDouble already defined!")
val getDouble: java.lang.Double = callback("getDouble")
val getDoubleType = getDouble.getClass()
System.out.println(getDoubleType)
System.out.println(getDouble)
if(getDoubleType != classOf[java.lang.Double])
	throw new Exception("getDouble not Double!")
callback("setDouble",getDouble)

System.out.println("getDoubleVector")
if(getDoubleVector != null)
	throw new Exception("getDoubleVector already defined!")
val getDoubleVector: Array[Double] = callback("getDoubleVector")
val getDoubleVectorType = getDoubleVector(0).getClass()
System.out.println(getDoubleVectorType)
System.out.println(getDoubleVector)
if(getDoubleVectorType != classOf[Double])
	throw new Exception("getDoubleVector not Double!")
callback("setDoubleVector",getDoubleVector)

System.out.println("getDoubleVectorAsList")
if(getDoubleVectorAsList != null)
	throw new Exception("getDoubleVectorAsList already defined!")
val getDoubleVectorAsList: Array[Double] = callback("getDoubleVectorAsList")
val getDoubleVectorAsListType = getDoubleVectorAsList(0).getClass()
System.out.println(getDoubleVectorAsListType)
System.out.println(getDoubleVectorAsList)
if(getDoubleVectorAsListType != classOf[Double])
	throw new Exception("getDoubleVectorAsList not Double!")
callback("setDoubleVectorAsList",getDoubleVectorAsList)

System.out.println("getDoubleMatrix")
if(getDoubleMatrix != null)
	throw new Exception("getDoubleMatrix already defined!")
val getDoubleMatrix: Array[Array[Double]] = callback("getDoubleMatrix")
val getDoubleMatrixType = getDoubleMatrix(0)(0).getClass()
System.out.println(getDoubleMatrixType)
System.out.println(getDoubleMatrix)
if(getDoubleMatrixType != classOf[Double])
	throw new Exception("getDoubleMatrix not Double!")
callback("setDoubleMatrix",getDoubleMatrix)

System.out.println("getDoubleMatrixAsList")
if(getDoubleMatrixAsList != null)
	throw new Exception("getDoubleMatrixAsList already defined!")
val getDoubleMatrixAsList: Array[Array[Double]] = callback("getDoubleMatrixAsList")
val getDoubleMatrixAsListType = getDoubleMatrixAsList(0)(0).getClass()
System.out.println(getDoubleMatrixAsListType)
System.out.println(getDoubleMatrixAsList)
if(getDoubleMatrixAsListType != classOf[Double])
	throw new Exception("getDoubleMatrixAsList not Double!")
callback("setDoubleMatrixAsList",getDoubleMatrixAsList)

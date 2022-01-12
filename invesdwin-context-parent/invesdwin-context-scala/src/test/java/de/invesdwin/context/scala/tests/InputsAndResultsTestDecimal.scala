System.out.println("getDecimal")
if(getDecimal != null)
	throw new Exception("getDecimal already defined!")
val getDecimal = putDecimal
val getDecimalType = getDecimal.getClass()
System.out.println(getDecimalType)
System.out.println(getDecimal)
if(getDecimalType != classOf[java.lang.Double])
	throw new Exception("getDecimal not Double!")

System.out.println("getDecimalVector")
if(getDecimalVector != null)
	throw new Exception("getDecimalVector already defined!")
val getDecimalVector = putDecimalVector.asInstanceOf[Array[Double]]
val getDecimalVectorType = getDecimalVector(0).getClass()
System.out.println(getDecimalVectorType)
System.out.println(getDecimalVector)
if(getDecimalVectorType != classOf[Double])
	throw new Exception("getDecimalVector not Double!")

System.out.println("getDecimalVectorAsList")
if(getDecimalVectorAsList != null)
	throw new Exception("getDecimalVectorAsList already defined!")
val getDecimalVectorAsList = putDecimalVectorAsList.asInstanceOf[Array[Double]]
val getDecimalVectorAsListType = getDecimalVectorAsList(0).getClass()
System.out.println(getDecimalVectorAsListType)
System.out.println(getDecimalVectorAsList)
if(getDecimalVectorAsListType != classOf[Double])
	throw new Exception("getDecimalVectorAsList not Double!")

System.out.println("getDecimalMatrix")
if(getDecimalMatrix != null)
	throw new Exception("getDecimalMatrix already defined!")
val getDecimalMatrix = putDecimalMatrix.asInstanceOf[Array[Array[Double]]]
val getDecimalMatrixType = getDecimalMatrix(0)(0).getClass()
System.out.println(getDecimalMatrixType)
System.out.println(getDecimalMatrix)
if(getDecimalMatrixType != classOf[Double])
	throw new Exception("getDecimalMatrix not Double!")

System.out.println("getDecimalMatrixAsList")
if(getDecimalMatrixAsList != null)
	throw new Exception("getDecimalMatrixAsList already defined!")
val getDecimalMatrixAsList = putDecimalMatrixAsList.asInstanceOf[Array[Array[Double]]]
val getDecimalMatrixAsListType = getDecimalMatrixAsList(0)(0).getClass()
System.out.println(getDecimalMatrixAsListType)
System.out.println(getDecimalMatrixAsList)
if(getDecimalMatrixAsListType != classOf[Double])
	throw new Exception("getDecimalMatrixAsList not Double!")

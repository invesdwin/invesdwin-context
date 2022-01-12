System.out.println("getPercent")
if(getPercent != null)
	throw new Exception("getPercent already defined!")
val getPercent = putPercent
val getPercentType = getPercent.getClass()
System.out.println(getPercentType)
System.out.println(getPercent)
if(getPercentType != classOf[java.lang.Double])
	throw new Exception("getPercent not Double!")

System.out.println("getPercentVector")
if(getPercentVector != null)
	throw new Exception("getPercentVector already defined!")
val getPercentVector = putPercentVector.asInstanceOf[Array[Double]]
val getPercentVectorType = getPercentVector(0).getClass()
System.out.println(getPercentVectorType)
System.out.println(getPercentVector)
if(getPercentVectorType != classOf[Double])
	throw new Exception("getPercentVector not Double!")

System.out.println("getPercentVectorAsList")
if(getPercentVectorAsList != null)
	throw new Exception("getPercentVectorAsList already defined!")
val getPercentVectorAsList = putPercentVectorAsList.asInstanceOf[Array[Double]]
val getPercentVectorAsListType = getPercentVectorAsList(0).getClass()
System.out.println(getPercentVectorAsListType)
System.out.println(getPercentVectorAsList)
if(getPercentVectorAsListType != classOf[Double])
	throw new Exception("getPercentVectorAsList not Double!")

System.out.println("getPercentMatrix")
if(getPercentMatrix != null)
	throw new Exception("getPercentMatrix already defined!")
val getPercentMatrix = putPercentMatrix.asInstanceOf[Array[Array[Double]]]
val getPercentMatrixType = getPercentMatrix(0)(0).getClass()
System.out.println(getPercentMatrixType)
System.out.println(getPercentMatrix)
if(getPercentMatrixType != classOf[Double])
	throw new Exception("getPercentMatrix not Double!")

System.out.println("getPercentMatrixAsList")
if(getPercentMatrixAsList != null)
	throw new Exception("getPercentMatrixAsList already defined!")
val getPercentMatrixAsList = putPercentMatrixAsList.asInstanceOf[Array[Array[Double]]]
val getPercentMatrixAsListType = getPercentMatrixAsList(0)(0).getClass()
System.out.println(getPercentMatrixAsListType)
System.out.println(getPercentMatrixAsList)
if(getPercentMatrixAsListType != classOf[Double])
	throw new Exception("getPercentMatrixAsList not Double!")

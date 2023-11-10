val getBooleanMatrix: Array[Array[Boolean]] = callback("getBooleanMatrix")
if(getBooleanMatrix.length != 2)
	throw new Exception("getBooleanMatrix empty!")
callback[Unit]("setBooleanMatrix",getBooleanMatrix)

val getBooleanMatrixAsList: Array[Array[Boolean]] = callback("getBooleanMatrixAsList")
if(getBooleanMatrixAsList.length != 2)
	throw new Exception("getBooleanMatrixAsList empty!")
callback[Unit]("setBooleanMatrixAsList",getBooleanMatrixAsList)


val getByteMatrix: Array[Array[Byte]] = callback("getByteMatrix")
if(getByteMatrix.length != 2)
	throw new Exception("getByteMatrix empty!")
callback[Unit]("setByteMatrix",getByteMatrix)

val getByteMatrixAsList: Array[Array[Byte]] = callback("getByteMatrixAsList")
if(getByteMatrixAsList.length != 2)
	throw new Exception("getByteMatrixAsList empty!")
callback[Unit]("setByteMatrixAsList",getByteMatrixAsList)


val getCharacterMatrix: Array[Array[Char]] = callback("getCharacterMatrix")
if(getCharacterMatrix.length != 2)
	throw new Exception("getCharacterMatrix empty!")
callback[Unit]("setCharacterMatrix",getCharacterMatrix)

val getCharacterMatrixAsList: Array[Array[Char]] = callback("getCharacterMatrixAsList")
if(getCharacterMatrixAsList.length != 2)
	throw new Exception("getCharacterMatrixAsList empty!")
callback[Unit]("setCharacterMatrixAsList",getCharacterMatrixAsList)


val getDecimalMatrix: Array[Array[Double]] = callback("getDecimalMatrix")
if(getDecimalMatrix.length != 2)
	throw new Exception("getDecimalMatrix empty!")
callback[Unit]("setDecimalMatrix",getDecimalMatrix)

val getDecimalMatrixAsList: Array[Array[Double]] = callback("getDecimalMatrixAsList")
if(getDecimalMatrixAsList.length != 2)
	throw new Exception("getDecimalMatrixAsList empty!")
callback[Unit]("setDecimalMatrixAsList",getDecimalMatrixAsList)


val getDoubleMatrix: Array[Array[Double]] = callback("getDoubleMatrix")
if(getDoubleMatrix.length != 2)
	throw new Exception("getDoubleMatrix empty!")
callback[Unit]("setDoubleMatrix",getDoubleMatrix)

val getDoubleMatrixAsList: Array[Array[Double]] = callback("getDoubleMatrixAsList")
if(getDoubleMatrixAsList.length != 2)
	throw new Exception("getDoubleMatrixAsList empty!")
callback[Unit]("setDoubleMatrixAsList",getDoubleMatrixAsList)


val getFloatMatrix: Array[FloatArray] = callback("getFloatMatrix")
if(getFloatMatrix.length != 2)
	throw new Exception("getFloatMatrix empty!")
callback[Unit]("setFloatMatrix",getFloatMatrix)

val getFloatMatrixAsList: Array[FloatArray] = callback("getFloatMatrixAsList")
if(getFloatMatrixAsList.length != 2)
	throw new Exception("getFloatMatrixAsList empty!")
callback[Unit]("setFloatMatrixAsList",getFloatMatrixAsList)


val getIntegerMatrix: Array[IntArray] = callback("getIntegerMatrix")
if(getIntegerMatrix.length != 2)
	throw new Exception("getIntegerMatrix empty!")
callback[Unit]("setIntegerMatrix",getIntegerMatrix)

val getIntegerMatrixAsList: Array[IntArray] = callback("getIntegerMatrixAsList")
if(getIntegerMatrixAsList.length != 2)
	throw new Exception("getIntegerMatrixAsList empty!")
callback[Unit]("setIntegerMatrixAsList",getIntegerMatrixAsList)


val getLongMatrix: Array[LongArray] = callback("getLongMatrix")
if(getLongMatrix.length != 2)
	throw new Exception("getLongMatrix empty!")
callback[Unit]("setLongMatrix",getLongMatrix)

val getLongMatrixAsList: Array[LongArray] = callback("getLongMatrixAsList")
if(getLongMatrixAsList.length != 2)
	throw new Exception("getLongMatrixAsList empty!")
callback[Unit]("setLongMatrixAsList",getLongMatrixAsList)


val getPercentMatrix: Array[Array[Double]] = callback("getPercentMatrix")
if(getPercentMatrix.length != 2)
	throw new Exception("getPercentMatrix empty!")
callback[Unit]("setPercentMatrix",getPercentMatrix)

val getPercentMatrixAsList: Array[Array[Double]] = callback("getPercentMatrixAsList")
if(getPercentMatrixAsList.length != 2)
	throw new Exception("getPercentMatrixAsList empty!")
callback[Unit]("setPercentMatrixAsList",getPercentMatrixAsList)


val getShortMatrix: Array[Array[Short]] = callback("getShortMatrix")
if(getShortMatrix.length != 2)
	throw new Exception("getShortMatrix empty!")
callback[Unit]("setShortMatrix",getShortMatrix)

val getShortMatrixAsList: Array[Array[Short]] = callback("getShortMatrixAsList")
if(getShortMatrixAsList.length != 2)
	throw new Exception("getShortMatrixAsList empty!")
callback[Unit]("setShortMatrixAsList",getShortMatrixAsList)


val getStringMatrix: Array[Array[String]] = callback("getStringMatrix")
if(getStringMatrix.length != 2)
	throw new Exception("getStringMatrix empty!")
callback[Unit]("setStringMatrix",getStringMatrix)

val getStringMatrixAsList: Array[Array[String]] = callback("getStringMatrixAsList")
if(getStringMatrixAsList.length != 2)
	throw new Exception("getStringMatrixAsList empty!")
callback[Unit]("setStringMatrixAsList",getStringMatrixAsList)

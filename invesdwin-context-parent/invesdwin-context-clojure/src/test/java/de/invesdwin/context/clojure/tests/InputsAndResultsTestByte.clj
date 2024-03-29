(defmacro defined [& var]
    `(try 
        (or 
            (boolean (resolve ~@var)) 
            (not (nil? (eval ~@var)))
        ) (catch Exception e# false)
    )
)

(println "getByte")
(if (defined 'getByte)
	(throw (Exception. "getByte already defined"))
)
(def getByte putByte)
(def getByteType (class getByte))
(println getByteType)
(println getByte)
(if-not (= getByteType Byte)
	(throw (Exception. "getByte not Byte!"))
)

(println "getByteVector")
(if (defined 'getByteVector)
	(throw (Exception. "getByteVector already defined"))
)
(def getByteVector putByteVector)
(def getByteVectorType (class (get getByteVector 0)))
(println getByteVectorType)
(println getByteVector)
(if-not (= getByteVectorType Byte)
	(throw (Exception. "getByteVector not Byte!"))
)

(println "getByteVectorAsList")
(if (defined 'getByteVectorAsList)
	(throw (Exception. "getByteVectorAsList already defined"))
)
(def getByteVectorAsList putByteVectorAsList)
(def getByteVectorAsListType (class (get getByteVectorAsList 0)))
(println getByteVectorAsListType)
(println getByteVectorAsList)
(if-not (= getByteVectorAsListType Byte)
	(throw (Exception. "getByteVectorAsList not Byte!"))
)

(println "getByteMatrix")
(if (defined 'getByteMatrix)
	(throw (Exception. "getByteMatrix already defined"))
)
(def getByteMatrix putByteMatrix)
(def getByteMatrixType (class (get (get getByteMatrix 0) 0)))
(println getByteMatrixType)
(println getByteMatrix)
(if-not (= getByteMatrixType Byte)
	(throw (Exception. "getByteMatrix not Byte!"))
)

(println "getByteMatrixAsList")
(if (defined 'getByteMatrixAsList)
	(throw (Exception. "getByteMatrixAsList already defined"))
)
(def getByteMatrixAsList putByteMatrixAsList)
(def getByteMatrixAsListType (class (get (get getByteMatrixAsList 0) 0)))
(println getByteMatrixAsListType)
(println getByteMatrixAsList)
(if-not (= getByteMatrixAsListType Byte)
	(throw (Exception. "getByteMatrixAsList not Byte!"))
)

(defmacro defined [& var]
    `(try 
        (or 
            (boolean (resolve ~@var)) 
            (not (nil? (eval ~@var)))
        ) (catch Exception e# false)
    )
)

(println "getDouble")
(if (defined 'getDouble)
	(throw (Exception. "getDouble already defined!"))
)
(def getDouble putDouble)
(def getDoubleType (class getDouble))
(println getDoubleType)
(println getDouble)
(if-not (= getDoubleType Double)
	(throw (Exception. "getDouble not Double!"))
)

(println "getDoubleVector")
(if (defined 'getDoubleVector)
	(throw (Exception. "getDoubleVector already defined!"))
)
(def getDoubleVector putDoubleVector)
(def getDoubleVectorType (class (get getDoubleVector 0)))
(println getDoubleVectorType)
(println getDoubleVector)
(if-not (= getDoubleVectorType Double)
	(throw (Exception. "getDoubleVector not Double!"))
)

(println "getDoubleVectorAsList")
(if (defined 'getDoubleVectorAsList)
	(throw (Exception. "getDoubleVectorAsList already defined!"))
)
(def getDoubleVectorAsList putDoubleVectorAsList)
(def getDoubleVectorAsListType (class (get getDoubleVectorAsList 0)))
(println getDoubleVectorAsListType)
(println getDoubleVectorAsList)
(if-not (= getDoubleVectorAsListType Double)
	(throw (Exception. "getDoubleVectorAsList not Double!"))
)

(println "getDoubleMatrix")
(if (defined 'getDoubleMatrix)
	(throw (Exception. "getDoubleMatrix already defined!"))
)
(def getDoubleMatrix putDoubleMatrix)
(def getDoubleMatrixType (class (get (get getDoubleMatrix 0) 0)))
(println getDoubleMatrixType)
(println getDoubleMatrix)
(if-not (= getDoubleMatrixType Double)
	(throw (Exception. "getDoubleMatrix not Double!"))
)

(println "getDoubleMatrixAsList")
(if (defined 'getDoubleMatrixAsList)
	(throw (Exception. "getDoubleMatrixAsList already defined!"))
)
(def getDoubleMatrixAsList putDoubleMatrixAsList)
(def getDoubleMatrixAsListType (class (get (get getDoubleMatrixAsList 0) 0)))
(println getDoubleMatrixAsListType)
(println getDoubleMatrixAsList)
(if-not (= getDoubleMatrixAsListType Double)
	(throw (Exception. "getDoubleMatrixAsList not Double!"))
)
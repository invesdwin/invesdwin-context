(defmacro defined [& var]
    `(try 
        (or 
            (boolean (resolve ~@var)) 
            (not (nil? (eval ~@var)))
        ) (catch Exception e# false)
    )
)

(println "getShort")
(if (defined 'getShort)
	(throw (Exception. "getShort already defined!"))
)
(def getShort putShort)
(def getShortType (class getShort))
(println getShortType)
(println getShort)
(if-not (= getShortType Short)
	(throw (Exception. "getShort not Short!"))
)

(println "getShortVector")
(if (defined 'getShortVector)
	(throw (Exception. "getShortVector already defined!"))
)
(def getShortVector putShortVector)
(def getShortVectorType (class (get getShortVector 0)))
(println getShortVectorType)
(println getShortVector)
(if-not (= getShortVectorType Short)
	(throw (Exception. "getShortVector not Short!"))
)

(println "getShortVectorAsList")
(if (defined 'getShortVectorAsList)
	(throw (Exception. "getShortVectorAsList already defined!"))
)
(def getShortVectorAsList putShortVectorAsList)
(def getShortVectorAsListType (class (get getShortVectorAsList 0)))
(println getShortVectorAsListType)
(println getShortVectorAsList)
(if-not (= getShortVectorAsListType Short)
	(throw (Exception. "getShortVectorAsList not Short!"))
)

(println "getShortMatrix")
(if (defined 'getShortMatrix)
	(throw (Exception. "getShortMatrix already defined!"))
)
(def getShortMatrix putShortMatrix)
(def getShortMatrixType (class (get (get getShortMatrix 0) 0)))
(println getShortMatrixType)
(println getShortMatrix)
(if-not (= getShortMatrixType Short)
	(throw (Exception. "getShortMatrix not Short!"))
)

(println "getShortMatrixAsList")
(if (defined 'getShortMatrixAsList)
	(throw (Exception. "getShortMatrixAsList already defined!"))
)
(def getShortMatrixAsList putShortMatrixAsList)
(def getShortMatrixAsListType (class (get (get getShortMatrixAsList 0) 0)))
(println getShortMatrixAsListType)
(println getShortMatrixAsList)
(if-not (= getShortMatrixAsListType Short)
	(throw (Exception. "getShortMatrixAsList not Short!"))
)

(defmacro defined [& var]
    `(try 
        (or 
            (boolean (resolve ~@var)) 
            (not (nil? (eval ~@var)))
        ) (catch Exception e# false)
    )
)

(println "getBoolean")
(if (defined 'getBoolean)
	(throw (Exception. "getBoolean already defined!"))
)
(def getBoolean (callback "getBoolean"))
(def getBooleanType (class getBoolean))
(println getBooleanType)
(println getBoolean)
(if-not (= getBooleanType Boolean)
	(throw (Exception. "getBoolean not boolean!"))
)
(callback "setBoolean" getBoolean)

(println "getBooleanVector")
(if (defined 'getBooleanVector)
	(throw (Exception. "getBooleanVector already defined!"))
)
(def getBooleanVector (callback "getBooleanVector"))
(def getBooleanVectorType (class (get getBooleanVector 0)))
(println getBooleanVectorType)
(println getBooleanVector)
(if-not (= getBooleanVectorType Boolean)
	(throw (Exception. "getBooleanVector not boolean!"))
)
(callback "setBooleanVector" getBooleanVector)

(println "getBooleanVectorAsList")
(if (defined 'getBooleanVectorAsList)
	(throw (Exception. "getBooleanVectorAsList already defined!"))
)
(def getBooleanVectorAsList (callback "getBooleanVectorAsList"))
(def getBooleanVectorAsListType (class (get getBooleanVectorAsList 0)))
(println getBooleanVectorAsListType)
(println getBooleanVectorAsList)
(if-not (= getBooleanVectorAsListType Boolean)
	(throw (Exception. "getBooleanVectorAsList not boolean!"))
)
(callback "setBooleanVectorAsList" getBooleanVectorAsList)

(println "getBooleanMatrix")
(if (defined 'getBooleanMatrix)
	(throw (Exception. "getBooleanMatrix already defined!"))
)
(def getBooleanMatrix (callback "getBooleanMatrix"))
(def getBooleanMatrixType (class (get (get getBooleanMatrix 0) 0)))
(println getBooleanMatrixType)
(println getBooleanMatrix)
(if-not (= getBooleanMatrixType Boolean)
	(throw (Exception. "getBooleanMatrix not boolean!"))
)
(callback "setBooleanMatrix" getBooleanMatrix)

(println "getBooleanMatrixAsList")
(if (defined 'getBooleanMatrixAsList)
	(throw (Exception. "getBooleanMatrixAsList already defined!"))
)
(def getBooleanMatrixAsList (callback "getBooleanMatrixAsList"))
(def getBooleanMatrixAsListType (class (get (get getBooleanMatrixAsList 0) 0)))
(println getBooleanMatrixAsListType)
(println getBooleanMatrixAsList)
(if-not (= getBooleanMatrixAsListType Boolean)
	(throw (Exception. "getBooleanMatrixAsList not boolean!"))
)
(callback "setBooleanMatrixAsList" getBooleanMatrixAsList)

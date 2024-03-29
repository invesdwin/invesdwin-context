(defmacro defined [& var]
    `(try 
        (or 
            (boolean (resolve ~@var)) 
            (not (nil? (eval ~@var)))
        ) (catch Exception e# false)
    )
)

(println "getPercent")
(if (defined 'getPercent)
	(throw (Exception. "getPercent already defined!"))
)
(def getPercent (callback "getPercent"))
(def getPercentType (class getPercent))
(println getPercentType)
(println getPercent)
(if-not (= getPercentType Double)
	(throw (Exception. "getPercent not Double!"))
)
(callback "setPercent" getPercent)

(println "getPercentVector")
(if (defined 'getPercentVector)
	(throw (Exception. "getPercentVector already defined!"))
)
(def getPercentVector (callback "getPercentVector"))
(def getPercentVectorType (class (get getPercentVector 0)))
(println getPercentVectorType)
(println getPercentVector)
(if-not (= getPercentVectorType Double)
	(throw (Exception. "getPercentVector not Double!"))
)
(callback "setPercentVector" getPercentVector)

(println "getPercentVectorAsList")
(if (defined 'getPercentVectorAsList)
	(throw (Exception. "getPercentVectorAsList already defined!"))
)
(def getPercentVectorAsList (callback "getPercentVectorAsList"))
(def getPercentVectorAsListType (class (get getPercentVectorAsList 0)))
(println getPercentVectorAsListType)
(println getPercentVectorAsList)
(if-not (= getPercentVectorAsListType Double)
	(throw (Exception. "getPercentVectorAsList not Double!"))
)
(callback "setPercentVectorAsList" getPercentVectorAsList)

(println "getPercentMatrix")
(if (defined 'getPercentMatrix)
	(throw (Exception. "getPercentMatrix already defined!"))
)
(def getPercentMatrix (callback "getPercentMatrix"))
(def getPercentMatrixType (class (get (get getPercentMatrix 0) 0)))
(println getPercentMatrixType)
(println getPercentMatrix)
(if-not (= getPercentMatrixType Double)
	(throw (Exception. "getPercentMatrix not Double!"))
)
(callback "setPercentMatrix" getPercentMatrix)

(println "getPercentMatrixAsList")
(if (defined 'getPercentMatrixAsList)
	(throw (Exception. "getPercentMatrixAsList already defined!"))
)
(def getPercentMatrixAsList (callback "getPercentMatrixAsList"))
(def getPercentMatrixAsListType (class (get (get getPercentMatrixAsList 0) 0)))
(println getPercentMatrixAsListType)
(println getPercentMatrixAsList)
(if-not (= getPercentMatrixAsListType Double)
	(throw (Exception. "getPercentMatrixAsList not Double!"))
)
(callback "setPercentMatrixAsList" getPercentMatrixAsList)

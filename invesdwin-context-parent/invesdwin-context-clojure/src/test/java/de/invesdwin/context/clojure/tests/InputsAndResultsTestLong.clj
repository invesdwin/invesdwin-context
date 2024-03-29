(defmacro defined [& var]
    `(try 
        (or 
            (boolean (resolve ~@var)) 
            (not (nil? (eval ~@var)))
        ) (catch Exception e# false)
    )
)

(println "getLong")
(if (defined 'getLong)
	(throw (Exception. "getLong already defined!"))
)
(def getLong putLong)
(def getLongType (class getLong))
(println getLongType)
(println getLong)
(if-not (= getLongType Long)
	(throw (Exception. "getLong not Long!"))
)

(println "getLongVector")
(if (defined 'getLongVector)
	(throw (Exception. "getLongVector already defined!"))
)
(def getLongVector putLongVector)
(def getLongVectorType (class (get getLongVector 0)))
(println getLongVectorType)
(println getLongVector)
(if-not (= getLongVectorType Long)
	(throw (Exception. "getLongVector not Long!"))
)

(println "getLongVectorAsList")
(if (defined 'getLongVectorAsList)
	(throw (Exception. "getLongVectorAsList already defined!"))
)
(def getLongVectorAsList putLongVectorAsList)
(def getLongVectorAsListType (class (get getLongVectorAsList 0)))
(println getLongVectorAsListType)
(println getLongVectorAsList)
(if-not (= getLongVectorAsListType Long)
	(throw (Exception. "getLongVectorAsList not Long!"))
)

(println "getLongMatrix")
(if (defined 'getLongMatrix)
	(throw (Exception. "getLongMatrix already defined!"))
)
(def getLongMatrix putLongMatrix)
(def getLongMatrixType (class (get (get getLongMatrix 0) 0)))
(println getLongMatrixType)
(println getLongMatrix)
(if-not (= getLongMatrixType Long)
	(throw (Exception. "getLongMatrix not Long!"))
)

(println "getLongMatrixAsList")
(if (defined 'getLongMatrixAsList)
	(throw (Exception. "getLongMatrixAsList already defined!"))
)
(def getLongMatrixAsList putLongMatrixAsList)
(def getLongMatrixAsListType (class (get (get getLongMatrixAsList 0) 0)))
(println getLongMatrixAsListType)
(println getLongMatrixAsList)
(if-not (= getLongMatrixAsListType Long)
	(throw (Exception. "getLongMatrixAsList not Long!"))
)

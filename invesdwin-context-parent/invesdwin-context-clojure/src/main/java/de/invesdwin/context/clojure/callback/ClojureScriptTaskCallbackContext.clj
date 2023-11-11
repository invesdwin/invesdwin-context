(defn callback [^String methodName & parameters]
	(if-not (. bindings containsKey "clojureScriptTaskCallbackContextUuid") 
        (throw (Exception. "IScriptTaskCallback not available"))
    )
    (let [^de.invesdwin.context.clojure.callback.ClojureScriptTaskCallbackContext context (. de.invesdwin.context.clojure.callback.ClojureScriptTaskCallbackContext (getContext clojureScriptTaskCallbackContextUuid))]
	    (. context invoke methodName (into-array Object parameters))
    )
)
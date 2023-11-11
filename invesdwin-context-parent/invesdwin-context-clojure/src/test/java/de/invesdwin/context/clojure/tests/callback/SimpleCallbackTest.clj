(println "putUuid")
(println putUuid)

(import de.invesdwin.context.clojure.tests.callback.SimpleCallbackTest)
(def getSecretStaticImport (. SimpleCallbackTest getSecretStatic putUuid))
(println "getSecretStaticImport")
(println getSecretStaticImport)

(def getSecretStaticCallback (callback "getSecretStatic" putUuid))
(println "getSecretStaticCallback")
(println getSecretStaticCallback)

(def getSecretCallback (callback "getSecret" putUuid))
(println "getSecretCallback")
(println getSecretCallback)

(def getSecretExpressionCallback (callback "getSecretExpression" putUuid))
(println "getSecretExpressionCallback")
(println getSecretExpressionCallback)

(callback "voidMethod")
(ns graft.core
  (:require [clojure.contrib.string :as string]))

(defn uri->var [root uri]
  (let [parts (if (= "/" uri)
                ["" "ROOT"]
                (.split uri "/"))
        sym (symbol (last parts))
        ns (create-ns
            (symbol
             (string/butlast 1 (string/join "." (conj (butlast parts) (name root))))))]
    (if-let [v (ns-resolve ns sym)] v (ns-resolve (create-ns root) 'four-oh-four))))

(defn graft [root]
  (fn [{:keys [uri] :as req}]
    (try
     (let [var (uri->var root uri)
           fn (deref var)]
       (fn req))
     (catch Exception e
       ((deref (ns-resolve (create-ns root) 'five-hundred))
        (assoc req ::exception e))))))

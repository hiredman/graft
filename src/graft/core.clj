(ns graft.core
  (:require [clojure.string :as string]))

(defn uri->symbol [root uri]
  (let [parts (if (= "/" uri)
                ["" "ROOT"]
                (.split uri "/"))]
    (symbol
     (string/butlast
      1 (string/join "." (conj (butlast parts) (name root))))
     (.replaceAll (last parts) "\\." "_"))))

(defn graft [root]
  (fn [{:keys [uri] :as req}]
    (try
      (let [var (or (ns-resolve root (uri->symbol root uri)) (ns-resolve root 'four-oh-four))
            fn (deref var)]
        (fn req))
      (catch Exception e
        ((deref (ns-resolve root 'five-hundred))
         (assoc req ::exception e))))))

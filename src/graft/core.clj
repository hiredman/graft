(ns graft.core
  (:require [clojure.string :as string]))

(defn uri->symbol [root uri]
  (let [parts (if (= "/" uri)
                ["ROOT"]
                (remove empty? (.split uri "/")))]
    (symbol
     (string/join "." (cons (name root) (butlast parts)))
     (.replaceAll (last parts) "\\." "_"))))

(defn graft [root]
  (fn [{:keys [uri] :as req}]
    (let [handle-req (or (ns-resolve root (uri->symbol root uri))
                         (ns-resolve root 'four-oh-four)
                         (constantly nil))
          handle-ex (ns-resolve root 'five-hundred)]
      (if handle-ex
        (try
          (handle-req req)
          (catch Exception e
            (handle-ex (assoc req ::exception e))))
        (handle-req req)))))

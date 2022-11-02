(ns big-in-japan.storage
  (:require [monger.collection :as mc]
            [monger.core :as mg]))

(defn test-houses [mongo-uri]
  (let [{:keys [conn db]} (mg/connect-via-uri mongo-uri)]
    (mc/insert-and-return db "houses" {:name "John" :age 30})
    (mg/disconnect conn)))

(defn save-houses [db houses]
  (mc/insert-batch db "houses" houses))

(defn connect [uri]
  (mg/connect-via-uri uri))
(defn disconnect [connection]
  (mg/disconnect connection))
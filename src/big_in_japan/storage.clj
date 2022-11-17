(ns big-in-japan.storage
  (:require [monger.collection :as mc]
            [monger.core :as mg]
            [big-in-japan.conf :as conf]
            [clojure.tools.logging :as log]))

(defn save-houses [db houses]
  (when (seq houses)
    (mc/insert-batch db "houses" houses)))

(defn get-houses [db]
  (mc/count db "houses"))

(defn connect [uri]
  (mg/connect-via-uri uri))
(defn disconnect [connection]
  (mg/disconnect connection))

(defn connect-and-save-houses [uri houses]
  (when (seq houses)
    (let [{:keys [conn db]} (connect uri)]
      (save-houses db houses)
      (disconnect conn))))

(defn with-mongo [uri fn]
  (let [{:keys [conn db]} (connect uri)]
    (try
      (fn db)
      (catch Exception e
        (log/error "Error during mongo operation" (.getMessage e)))
      (finally (disconnect conn)))))


#_(with-mongo (conf/db-uri conf/config) get-houses)
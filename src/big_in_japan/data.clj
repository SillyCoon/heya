(ns big-in-japan.data
  (:require [big-in-japan.athome.houses :as ah])
  (:require [clojure.core.async :as a])
  (:require [big-in-japan.storage :as storage])
  (:require [big-in-japan.conf :as conf]))

(defonce distance-filters
         [{:id [:foot] :value 45 :init :minutes}
          {:id [:car] :value 10 :unit :minutes}
          {:id [:car] :value 10 :unit :km}
          {:id [:bus-stop :foot] :value 20 :unit :minutes}
          {:id [:ride] :value 20 :unit :minutes}])

(defn get-prefecture [url prefecture]
  (a/thread
    (ah/get-athome-by-prefecture
      url
      prefecture
      {:location distance-filters :price 5000000})))

(defn get-prefectures-range [url range]
  (let [pref-chan (a/merge (map #(get-prefecture url %) range))]
    (a/go-loop [result []]
      (let [pref (a/<! pref-chan)]
        (if (nil? pref)
          result
          (recur (concat result pref)))))))

#_(count (a/<!! (get-prefectures-range (conf/prefecture-url conf/config) [45 46])))


(defn get-and-save-range [url db-uri range]
  (let [houses-chan (get-prefectures-range url range)
        {:keys [conn db]} (storage/connect db-uri)]
    (a/go
      (println (.getName (Thread/currentThread)))
      (println range)
      (storage/save-houses db (a/<! houses-chan))
      (storage/disconnect conn))))

(defn get-and-save-prefectures [url db-uri]
  (doseq [range (partition 5 (rest (range 47)))]
    (get-and-save-range url db-uri  range)))


#_ (get-and-save-prefectures (conf/prefecture-url conf/config) (conf/db-uri conf/config))
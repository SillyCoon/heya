(ns big-in-japan.data
  (:require [big-in-japan.athome.houses :as ah])
  (:require [clojure.core.async :as a])
  (:require [big-in-japan.storage :as storage]))

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
          (recur (merge result pref)))))))

(defn get-and-save-prefecture [url pref-num db-uri]
  (let [prefecture (ah/get-athome-by-prefecture url pref-num {:location distance-filters :price 5000000})
        {:keys [conn db]} (storage/connect db-uri)]
    (storage/save-houses db prefecture)
    (storage/disconnect conn)))
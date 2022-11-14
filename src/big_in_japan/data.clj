(ns big-in-japan.data
  (:require [big-in-japan.athome.houses :as ah])
  (:require [clojure.core.async :as a])
  (:require [big-in-japan.storage :as storage])
  (:require [big-in-japan.conf :as conf]
            [clojure.string :as str]))

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
#_(println (count (a/<!! (get-prefecture (conf/prefecture-url conf/config) 46))))

(defn get-prefectures-range [url range]
  (let [pref-chan (a/merge (map #(get-prefecture url %) range))]
    (a/go-loop [result []]
      (let [pref (a/<! pref-chan)]
        (if (nil? pref)
          result
          (recur (concat result pref)))))))
#_(count (a/<!! (get-prefectures-range (conf/prefecture-url conf/config) [45 46])))

(defn get-prefectures-range-serial [url range]
  (loop [result []
         rg range]
    (let [pref (a/<!! (get-prefecture url (first rg)))
          accum (concat result pref)
          _ (println "prefectures fetched for range" (first rg) "; Amount:" (count pref))]
      (if-not (seq (rest rg))
        accum
        (recur accum (rest rg))))))

  #_(count (get-prefectures-range-serial (conf/prefecture-url conf/config) [45 46]))

(defn get-and-save-range [url db-uri range]
  (a/thread
    (storage/connect-and-save-houses
      db-uri
      (get-prefectures-range-serial url range))))

(defn get-and-save-prefectures [url db-uri]
  (doseq [range (partition 5 (rest (range 47)))]
    (a/go
      (a/<! (get-and-save-range url db-uri range))
      (println "Done for range:" (str/join ", " range)))))

#_(get-and-save-prefectures (conf/prefecture-url conf/config) (conf/db-uri conf/config))
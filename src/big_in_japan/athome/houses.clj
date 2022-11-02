(ns big-in-japan.athome.houses
  (:require [big-in-japan.utils :refer [get-hickory mans-to-number select-houses]])
  (:require [big-in-japan.athome.location :refer [validate-location]])
  (:require [hickory.select :as s]))

(def telework-params
  {:search_type "freeword"
   :freeword    "＃テレワーク"})

(def default-params
  {:br_kbn      "buy"
   :sbt_kbn     "buy"
   :page        1
   :search_sort "kokai_date"
   :item_count  500})

(defn athome-houses-hickory
  ([url] (select-houses "propety" (get-hickory url default-params)))
  ([url filter-params] (select-houses
                         "propety"
                         (get-hickory url (concat filter-params default-params)))))

(defn parse-link [house]
  (-> (s/select (s/class "sp") house)
      first
      :attrs
      :href))

(defn parse-title [house]
  (-> (s/select (s/child
                  (s/class "propetyTitle")
                  (s/tag :a))
                house)
      first
      :content
      first))

(defn parse-details [house]
  (map (comp first :content)
       (drop 1 (s/select (s/descendant
                           (s/class "detailOuter")
                           (s/tag :dd)) house))))
#_(parse-details (first (athome-houses-mem)))

(defn parse-price [house]
  (-> (s/select (s/descendant
                  (s/class "price")
                  (s/tag :span))
                house)
      first
      :content
      first
      mans-to-number))
#_(parse-price (first (athome-houses-mem)))

(defn parse-detail [house]
  (let [price (parse-price house)
        [house-area land-area _ _ _ built location station] (parse-details house)]
    {:price      price
     :house_area house-area
     :land_area  land-area
     :built      built
     :location   location
     :station    station
     }))
#_(parse-detail (first (athome-houses-mem)))

(defn parse-house [house]
  {
   :title   (parse-title house)
   :link    (parse-link house)
   :details (parse-detail house)
   })

#_(parse-house (first (athome-houses-mem)))

(defn parse-houses [houses]
  (map parse-house houses))

(defn validate-price [house max-price]
  (if max-price
    (let [house-price (-> house :details :price)]
      (when
        (number? house-price)
        (<= house-price max-price)))
    true))
#_(validate-price {:details {:price 100.1}} 1000)

(defn apply-filters [houses filters]
  (if (empty? filters)
    houses
    (filter (fn [h] (and (validate-location (-> h :details :station)
                                            (:location filters))
                         (validate-price h (:price filters)))) houses)))

(defn get-athome-telework [url filters]
  (let [houses (parse-houses (athome-houses-hickory url telework-params))
        filtered-houses (apply-filters houses filters)]
    filtered-houses))
#_(get-athome-telework [])

(defn get-athome-by-prefecture
  [url prefecture filters]
  (let [houses (parse-houses (athome-houses-hickory
                               (str url prefecture)))
        filtered-houses (apply-filters houses filters)]
    filtered-houses))
#_(get-athome-by-prefecture 46 [])
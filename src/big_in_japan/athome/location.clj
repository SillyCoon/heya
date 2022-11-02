(ns big-in-japan.athome.location
  (:require [clojure.set :as set])
  (:require [clojure.string :as str])
  (:require [big-in-japan.utils :as u]))

(def by-car-minutes #"(車)(\d+)(分)")
(def by-car-km #"(車)(\d+.?\d*)(km)")
(def on-foot-station-minutes #"(徒)(歩)(\d+)(分)")
(def on-foot-bus-minutes #"(停)(歩)(\d+)(分)")
(def on-foot-bus-minutes-ride-minutes #"(停)(歩)(\d+)(分) (乗車)(\d+)(分)")

(def mapping {"車"   :car
              "分"   :minutes
              "徒"   :station
              "停"   :bus-stop
              "歩"   :foot
              "乗車" :ride
              "km"   :km})
(def rev-map (set/map-invert mapping))

(defn to-regex [{:keys [id unit]}]
  (let [regex-id (str/join (map #(rev-map % %) id))
        regex-amount "(\\d+)"
        regex-unit (rev-map unit unit)]
    (re-pattern (str regex-id regex-amount regex-unit))))
#_(to-regex {:id [:bus-stop :foot] :value 10 :unit :minutes})

; need to return filter too

(defn parse-for-compare [location [regex upper-bound]]
  (let [value (u/parse-num location regex)]
    (when value [value upper-bound])))
#_(parse-for-compare "停歩bled分" [#"停歩(\d+)分" 10])


(defn validate-location [location filters]
  (let [regexs
        (map (fn [f] [(to-regex f) (:value f)]) filters)

        [value upper-bound]
        (some #(parse-for-compare location %) regexs)]
  (when value (<= value upper-bound))))

#_(validate-location "停歩bled分" [{:id [:foot] :value 45 :unit :minutes}
                                      {:id [:car] :value 10 :unit :minutes}])
#_(validate-location "停歩10分" [{:id [:foot] :value 45 :unit :minutes}
                                   {:id [:car] :value 10 :unit :minutes}])

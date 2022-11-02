(ns big-in-japan.core
  (:require [big-in-japan.long-polling :refer [run-polling]] )
  (:require [cprop.core :refer [load-config]]))

(def -config (load-config))

#_ (house-view
  (translate-house (first (get-athome-houses-mem))
                   [:title
                    [:details :location]
                    [:details :station]]))


#_(run-polling -config)
#_(updates-by-offset tg/telegram (u/load-offset offset-path) 1000)
(ns big-in-japan.conf
  (:require [cprop.core :refer [load-config]]))

(def config (load-config))

(defn prefecture-url [config] (-> config :houses :prefecture-url))

(defn db-uri [config] (-> config :db :uri))
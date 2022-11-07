(ns big-in-japan.utils
  (:require [org.httpkit.client :as client])
  (:require [hickory.select :as s])
  (:require [hickory.core :as hc]
            [clojure.string :as str]))

(defn parse-response [{:keys [status body error]}]
  (if-not error
    (-> body hc/parse hc/as-hickory)
    (throw (ex-info "HTML GET error" {:status status :error error}))))

(defn get-hickory
  ([url] (-> @(client/request {:url url :connect-timeout 600000 :idle-timeout 600000}) parse-response))
  ([url params] (-> @(client/request {:url url :query-params params}) parse-response)))

(defn select-houses [house-class hickory-html]
  (s/select (s/class house-class) hickory-html))

(defn update-by-keys [mp ks f]
  (reduce #(update-in % (if (coll? %2) %2 [%2]) f) mp ks))

#_ (update-by-keys {:a 1 :b {:c 3}} [:a [:b :c]] inc)

(defn save-offset [offset-file offset]
  (spit offset-file (str offset)))

#_(save-offset offset-file 123)

(defn load-offset [offset-file]
  (try
    (-> offset-file slurp Long/parseLong)
    (catch Throwable _
      nil)))

#_(load-offset offset-file)

(defn mans-to-number [mans]
  (try (-> (str/replace mans #"[\.,]" "")
      Integer/parseInt
      (* 10000))
       (catch NumberFormatException _ mans)))
#_ (mans-to-number "相談")

(defmacro catch-or
  [this on-error-this] `(try ~this (catch Exception _# ~on-error-this)))

(defn parse-num [string regex]
  (let [[_ value] (re-find regex string)]
    (catch-or (Float/parseFloat value) value)))
#_(parse-num "停歩1分" #"停歩(\d+)分")

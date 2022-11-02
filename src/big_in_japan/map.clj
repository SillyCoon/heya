(ns big-in-japan.map
  (:require [org.httpkit.client :as client])
  (:require [cheshire.core :as json]))

(defn get-coordinates [url object]
  (let [response @(client/request {:url          url
                                   :user-agent   "bot Big in Japan"
                                   :query-params {:q      object
                                                  :limit  1
                                                  :format "json"}})
        {:keys [lon lat]} (json/parse-string (:body response) true)]
    {:longitude lon :latitude lat}))

#_(get-coordinates "兵庫県赤穂郡上郡町高田台１丁目")

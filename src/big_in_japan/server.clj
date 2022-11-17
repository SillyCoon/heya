(ns big-in-japan.server
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [defroutes GET]]))

(defroutes routes
  (GET "/" [] (fn [request] {:code 200 :body "Sooka"})))

(defonce http-server (atom nil))

(defn stop-server []
  (when-not (nil? @http-server)
    (@http-server :timeout 100)
    (reset! http-server nil)))

#_(stop-server)

#_(reset! http-server (server/run-server #'routes {:port 9999}))

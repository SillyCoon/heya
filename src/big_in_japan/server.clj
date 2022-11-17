(ns big-in-japan.server
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [defroutes GET] :as compojure]))

(defroutes api-routes
           (compojure/context "/api/v1" []
             (GET "/processing" [] (fn [_] {:code 200 :body "Processing..."}))
             (GET "/health" [] (fn [_] {:code 200 :body "Healthy"}))))

(defonce http-server (atom nil))

(defn stop-server []
  (when-not (nil? @http-server)
    (@http-server :timeout 100)
    (reset! http-server nil)))

#_(stop-server)

#_(reset! http-server (server/run-server #'api-routes {:port 9999}))

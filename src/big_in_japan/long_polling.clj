(ns big-in-japan.long-polling
  (:require
    [cheshire.core :as json]
    [clojure.tools.logging :as log]
    [big-in-japan.utils :as u]
    [big-in-japan.commands :as c]
    [big-in-japan.telegram-bot :as tg]))

(defmulti reply (fn [_ options] (:type options)))
(defmethod reply :message
  ([telegram {:keys [chat-id text] :as options}]
   (tg/send-message telegram chat-id text options)))
(defmethod reply :location
  ([telegram {:keys [chat-id] :as options}]
   (tg/send-location telegram chat-id options)))

#_ (reply tg/telegram { :chat-id 218269790 :latitude 10 :longitude 10 :type :location })

(defn updates-by-offset [telegram offset timeout]
  (let [updates (tg/get-updates telegram
                                {:offset  offset
                                 :timeout timeout})
        new-offset (or (some-> updates peek :update_id inc)
                       offset)]
    [new-offset updates]))

(defn run-polling
  [config]

  (let [{:keys [polling telegram]} config
        {:keys [update-timeout offset-file]} polling

        offset (u/load-offset offset-file)]

    (loop [offset offset]

      (let [[new-offset updates]
            (updates-by-offset telegram offset update-timeout)
            commands (c/parse-commands updates)
            responses (flatten (map #(c/handle-command % config) commands))]

        (log/infof "Got %s commands, next offset: %s, commands: %s"
                   (count commands)
                   new-offset
                   (json/generate-string commands {:pretty true}))

        (run! (fn [options]
                (reply telegram options)) responses)

        (when offset
          (u/save-offset offset-file new-offset))

        (recur new-offset)))))
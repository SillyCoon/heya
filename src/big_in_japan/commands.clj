(ns big-in-japan.commands
  (:require [big-in-japan.athome.houses :as ah]
            [big-in-japan.map :as mp]
            [big-in-japan.translate :as t]
            [big-in-japan.view :as view]))

(def filter-commands
  (filter (fn [u] (= (some-> u :message :entities first :type) "bot_command"))))

(def map-commands
  (map (fn [{{:keys [message_id chat text]} :message}]
         {:message-id message_id :chat-id (:id chat) :command (keyword (subs text 1))})))

(defn parse-commands [updates]
  (transduce (comp filter-commands map-commands) conj updates))

(defmulti handle-command (fn [command _] (:command command)))

(defmethod handle-command :start
  ([{:keys [chat-id]} _]
   {:chat-id chat-id :text "Welcome" :type :message}))

(defmethod handle-command :help
  ([{:keys [chat-id]} _]
   {:chat-id chat-id :text "Use /house to show latest available house" :type :message}))

(defmethod handle-command :house
  ([{:keys [chat-id]} config]
   (let [houses (ah/get-athome-telework (-> config :houses :prefecture-url) [])
         house (last houses)
         coords (mp/get-coordinates (-> config :geocoder :url) (-> house :details :location))
         translated-house (t/translate-house config house [:title [:details :station] [:details :location]])
         view (view/house-view translated-house)]
     [{:chat-id chat-id :text view :parse-mode "HTML" :type :message}
      (merge {:chat-id chat-id :type :location} coords)])
   ))

(defmethod handle-command :default
  ([{:keys [chat-id]} _]
   {:chat-id chat-id :text "No such command!" :type :message}))

#_(handle-command {:command :start})

#_(handle-command {:command :start :chat-id 218269790 :message-id "kek"})

#_(handle-command {:command :house})
(ns big-in-japan.commands-test
  (:require [clojure.test :refer :all])
  (:require [big-in-japan.commands :refer [parse-commands handle-command]]))

(defonce mock-commands [{:update_id 203144280,
                         :message   {:message_id 61,
                                     :from       {:id            203567490,
                                                  :is_bot        false,
                                                  :first_name    "Aleksey",
                                                  :last_name     "Ivanov",
                                                  :username      "SillyCoon",
                                                  :language_code "en"},
                                     :chat       {:id 203567490, :first_name "Aleksey", :last_name "Ivanov", :username "SillyCoon", :type "private"},
                                     :date       1663983988,
                                     :text       "/start",
                                     :entities   [{:offset 0, :length 4, :type "bot_command"}]}}])

(deftest parse-commands-test
  (is (=
        [{:chat-id    203567490
          :command    :start
          :message-id 61}]
        (parse-commands mock-commands))))

(deftest handle-command-test
  (is (= {:chat-id nil :text "Welcome" :type :message}
         (handle-command {:command :start} {})))
  (is (= {:chat-id nil :text "Use /house to show latest available house" :type :message}
         (handle-command {:command :help} {})))
  (is (= {:chat-id nil :text "No such command!" :type :message}
         (handle-command {:command :no-such-command} {}))))

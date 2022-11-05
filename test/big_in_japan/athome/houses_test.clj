(ns big-in-japan.athome.houses-test
  (:require [clojure.test :as test])
  (:require [big-in-japan.conf :as conf])
  (:require [big-in-japan.athome.houses :refer [get-athome-by-prefecture]]))

(def test-url (conf/prefecture-url conf/config))

(test/deftest get-athome-by-prefecture-test
  (test/is (seq (get-athome-by-prefecture test-url 46))))

(ns big-in-japan.data-test
  (:require [clojure.test :as test])
  (:require [clojure.core.async :as async])
  (:require [big-in-japan.conf :as conf])
  (:require [big-in-japan.data :refer [get-prefectures-range]]))

(test/deftest get-prefectures-range-test
  (test/is (seq (async/<!! (get-prefectures-range (conf/prefecture-url conf/config) [45 46])))))

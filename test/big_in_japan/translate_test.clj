(ns big-in-japan.translate-test
  (:require [big-in-japan.translate :as tr])
  (:require [cprop.core :refer [load-config]])
  (:require [clojure.test :refer :all]))

(def test-config (load-config))

(deftest translate-memo-test
  (is (= (tr/translate-memo (:translation test-config) "採用育成部")
         "Recruitment and Training Department")))
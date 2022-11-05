(ns big-in-japan.core)


;(def ch (get-prefectures-range (by-prefecture-url -config) [43 44 45 46]))
;(async/<!! ch)


#_(run-polling -config)
#_(updates-by-offset tg/telegram (u/load-offset offset-path) 1000)
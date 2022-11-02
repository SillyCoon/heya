(ns big-in-japan.translate
  (:require
    [org.httpkit.client :as client]
    [cheshire.core :as json]
    [big-in-japan.utils :as u]))

(defn deepl-auth-header [key]
  {"Authorization" (str "DeepL-Auth-Key " key)})

(defn make-translate-body [text]
  {
   "text"        text
   "target_lang" "EN-US"
   "source_lang" "JA"
   })

(defn request-translate [{:keys [url key]} text]
  (-> @(client/request {:url         url
                        :method      :post
                        :timeout     3000
                        :form-params (make-translate-body text)
                        :headers     (deepl-auth-header key)})
      :body
      json/parse-string))

(defn translate
  ([config] (partial translate config))
  ([config text]
   (let [response (request-translate config text)
         [translation] (get response "translations")]
     (get translation "text"))
   ))

(def translate-memo (memoize translate))

(defn translate-house [config house keys]
  (u/update-by-keys house keys (translate-memo config)))

#_(translate-house {:title "採用育成部" :kek "採用育成部" :price 10000} [:title :kek])
#_(translate "採用育成部 人材開発課から下記研修にご登録頂いている皆様へリマインドのご連絡です")
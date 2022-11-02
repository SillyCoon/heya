(ns big-in-japan.view)

(defn bold [text]
  (str "<b>" text "</b>"))

(defn italic [text]
  (str "<i>" text "</i>"))

(def new-str "\n")

(defn title-view [name value]
  (str (bold name) ": " value))
(defn detail-view [name value]
  (str name ": " (italic value)))

(defn details-view [{:keys [price
                            house_area
                            land_area
                            built
                            location
                            station]}]
  (str (detail-view "Price" price) new-str
       (detail-view "Location" location) new-str
       (detail-view "Station" station) new-str
       (detail-view "Built" built) new-str
       (detail-view "House Area" house_area) new-str
       (detail-view "Land Area" land_area))
  )

(defn house-view [{:keys [title link details]}]
  (str (title-view "Title" title) new-str
       (title-view "Link" link) new-str
       new-str
       (title-view "Details" "") new-str
       (details-view details)))



#_(house-view {:title   "House 1"
               :link    "https://localhost"
               :details {
                         :price      "1000"
                         :location   "Nagasaki"
                         :station    "JR Yoga, 10 minutes on foot"
                         :built      "2022-08-11"
                         :house_area "100.2m"
                         :land_area  "300m"
                         }})
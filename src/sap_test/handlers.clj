(ns sap-test.handlers
  (:require [cheshire.core :refer [generate-string parse-string]]
            [clojure.java.io :refer [reader writer]]
            [clojure.string :refer [split]]
            [clj-time.format :as ctime]
            [clj-time.core :as t]))

(def resource-path "resources/data.txt")

(defn read-str-to-maps [str-list]
  (reduce #(conj %1 (read-string %2)) [] str-list))

(defn read-data []
  (with-open [rdr (reader resource-path)]
    (-> rdr
        line-seq
        first
        (split #";")
        read-str-to-maps)))

(defn filter-before-two-seconds [item]
  (let [date (-> item
                 (get-in [:item :timestamp])
                 ctime/parse)
        after? (t/after? date (t/minus (t/now) (t/seconds 100)))
        before? (t/before? date (t/now))]
    (and before? after?)))

(defn get-handler []
  (let [data (read-data)
        item-list-last-two-seconds (filter #(filter-before-two-seconds %) data)
        item-list-last-hundred (take 100 (reverse data))] ;;Reverse is to take last n items posted.
    (if (> (count item-list-last-hundred) (count item-list-last-two-seconds))
      (generate-string item-list-last-hundred {:pretty true})
      (generate-string item-list-last-two-seconds {:pretty true}))))

(defn post-handler [ctx]
  (with-open [w (writer resource-path :append true)]
    (.write w (str (parse-string (slurp (get-in ctx [:request :body])) true) ";"))))
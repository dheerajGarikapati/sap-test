(ns sap-test.core
  (:require [ring.adapter.jetty :as jetty]
            [sap-test.routes :as sap-routes]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn -main
  [& args]
  (-> sap-routes/post-routes
      (wrap-defaults  (assoc-in site-defaults [:security :anti-forgery] false))
      (jetty/run-jetty {:port  8080
                        :join? false}))
  (println "Server Started"))

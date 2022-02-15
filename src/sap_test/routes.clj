(ns sap-test.routes
  (:require [compojure.core :refer [defroutes ANY]]
            [sap-test.handlers :as handlers]
             [liberator.core :refer [defresource]]))

(declare api-resource)

(defresource api-resource
  :available-media-types ["application/json"]
  :allowed-methods [:get :post]
  :post! (fn [ctx] (handlers/post-handler ctx))
  :handle-ok (fn [_] (handlers/get-handler))
  :handle-created (fn [_] "Item Posted."))

(defroutes post-routes
  (ANY "/items" [_] api-resource))
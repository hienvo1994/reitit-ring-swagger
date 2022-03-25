(ns reitit-ring-swagger.router
  (:require [clojure.java.io :as io]
            [reitit.ring.middleware.multipart :as multipart]))

(def image-router
  ["/image"
   {:swagger {:tags ["files"]}}

   ["/upload"
    {:post {:summary    "upload an image"
            :parameters {:multipart {:file multipart/temp-file-part}}
            :responses  {200 {:body {:name string?, :size int?}}}
            :handler    (fn [{{{:keys [file]} :multipart} :parameters}]
                          (.mkdir (io/file "resources/uploads")) ;; For first run
                          (io/copy
                            (:tempfile file) ;; location of the downloaded file
                            (io/file (str "resources/uploads/" (:filename file))))
                          {:status 200
                           :body   {:name (:filename file)
                                    :size (:size file)}})}}]

   ["/load"
    {:get {:summary "load an image"
           :swagger {:produces ["image/png"]}
           :handler (fn [_]
                      {:status 200
                       :headers {"Content-Type" "image/png"}
                       :body (-> "reitit.png"
                                 (io/resource)
                                 (io/input-stream))})}}]])

(def math-router
  ["/math"
   {:swagger {:tags ["math"]}}

   ["/plus"
    {:get {:summary "plus with spec query parameters"
           :parameters {:query {:x int?
                                :y int?}}
           :responses {200 {:body {:total int?}}}
           :handler (fn [{{{:keys [x y]} :query} :parameters}]
                      {:status 200
                       :body {:total (+ x y)}})}
     :post {:summary "plus with spec body parameters"
            :parameters {:body {:x int?
                                :y int?}}
            :responses {200 {:body {:total int?}}}
            :handler (fn [{{{:keys [x y]} :body} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}}]])

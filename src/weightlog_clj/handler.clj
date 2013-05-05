(ns weightlog-clj.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [weightlog-clj.data :as data]))

(defn timestamp-as-iso 
  "convert a java.sql.Timestamp to an iso date string, used in json serialization"
  [ts]
  (String/format "%tFT%<tRZ"(to-array (list ts))))

(defn- -write-ts-json 
  [ts #^java.io.PrintWriter out]
  (.print out (str "\"" (timestamp-as-iso ts) "\"")))

(extend java.sql.Timestamp json/JSONWriter
        {:-write -write-ts-json})
(extend java.util.UUID json/JSONWriter
        {:-write (fn [uuid  #^java.io.PrintWriter out]
                   (.print out (str "\""(.toString uuid) "\"")))})

(defn json-error [status map]
  (println status map)
  {:status status
   :headers {"Content-Type" "application/json"}
   :body (json/write-str map)})
(defn json-response [map]
  (if (> (count (:errors map)) 0) (json-error 400 map)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/write-str map)}))

(defn login 
  [email password]
  (let [u (data/user-by-email email)
        valid (data/auth-user u password)]
    (if valid {:session (data/create-session u) :errors []}
    {:errors [:bad_login]})))
  

  (defroutes app-routes
    (GET "/" [] (json-response {:login "/login"}))
    (POST "/login" [email password] (json-response (login email password)))
    (route/not-found (json/write-str {:errors ["not-found"]})))

(def app
  (handler/api app-routes))

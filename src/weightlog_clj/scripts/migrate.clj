(ns weightlog-clj.scripts.migrate
  (:import [javax.sql DataSource])
  (:import [org.apache.commons.dbcp BasicDataSource])
  (:import [com.googlecode.flyway.core Flyway]))

(defn data-source [db-conf]
  ;(println "making data-source for " db-conf)
  (doto (BasicDataSource.)
    (.setDriverClassName (:driver db-conf))
    (.setUrl (:url db-conf))
    (.setUsername (:user db-conf))
    (.setPassword (:password db-conf))))

(defn flyway [db-conf]
  (let [f (Flyway.)]
    (doto f
      (.setDataSource (:url db-conf) (:user db-conf) (:password db-conf))
      (.setInitOnMigrate true)
      (.setEncoding "UTF-8"))
    f))

(defn parse-database-url [url]
  (let [[_ user password host port db] (re-matches #"postgres://(?:(.+):(.*)@)?([^:]+)(?::(\d+))?/(.+)" url)]
    {:user user
     :password password
     :host host
     :port port
     :db db
     :driver "org.postgresql.Driver"}))

(def dev-db {:user "weightlog"
             :password "weightlog"
             :driver "org.postgresql.Driver"
             :url "jdbc:postgresql://localhost:5432/weightlog"})

(def test-db (assoc dev-db :url "jdbc:postgresql://localhost:5432/weightlog_test"))

(defn prod-db []
  (let [db-url (System/getenv "DATABASE_URL")]
    (if (empty? db-url) 
      nil
      (let [db-map (parse-database-url db-url)]
        {:user (:user db-map)
         :password (:password db-map)
         :driver "org.postgresql.Driver"
         :url (str "jdbc:postgresql://" (:host db-map) ":" (:port db-map) "/" (:db db-map))}))))

(def databases {"dev" dev-db
 "test" test-db
 "prod" (prod-db)})

(defn clean [f]
  (let [conn (.getConnection (.getDataSource f))
        stmt (.createStatement conn)]
    (.execute stmt "DROP EXTENSION IF EXISTS \"uuid-ossp\" CASCADE;")
    (.close conn)
    (.clean f)))
    

(defn -main [& args]
  (let [env (nth args 0 "dev")
        db (get databases env)
        target (nth args 1 nil)]
    (if (nil? db)
      (do (println "Error: database " env " not found")
          (System/exit 1))
      (let [f (flyway db)]
        (println "target: " target)
        (cond 
         (= target "clean") (clean f)
         (= target "placeholders") (println (.getPlaceholders f))
         (= target "locations") (println (seq (.getLocations f)))
         target (do (.setTarget f target)
                    (.migrate f))
         true (.migrate f))))))
               
  

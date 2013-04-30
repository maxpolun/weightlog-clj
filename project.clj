(defproject weightlog-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.5"]
                 [org.clojure/data.json "0.2.2"]
                 [korma "0.3.0-RC5"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [clj-bcrypt-wrapper "0.1.0"]
                 [com.googlecode.flyway/flyway-core "2.1.1"]
                 [commons-dbcp/commons-dbcp "1.2.2"]
                 [crypto-random "1.1.0"]]
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler weightlog-clj.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})

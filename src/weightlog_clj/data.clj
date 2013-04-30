(ns weightlog-clj.data
  (:use korma.db korma.core)
  (:require [clj-bcrypt-wrapper.core :as bcrypt]
            [crypto.random :as rand]))

(defdb dev (postgres {:db "weightlog"
                      :user "weightlog"
                      :password "weightlog"
                      :host "localhost"
                      :port "5432"}))

(defmacro with-transaction 
  "run body in a transaction and roll it back"
  [& body]
  `(transaction 
    (do ~@body
       (rollback))))

(defentity users 
  (entity-fields :id :email :pw_hash))

(defentity sessions
  (entity-fields :id :user_id :created_at)
  (belongs-to users))

(defentity exersizes
  (entity-fields :name))

(defentity sets
  (entity-fields :id :completed_at :exersize :reps :user_id :notes :weight :unit)
  (belongs-to users)
  (belongs-to exersizes {:fk :exersize}))

(defn user-by-id
  [id]
  (first (select users (where {:id id}))))

(defn user-by-email
  [email]
  (first (select users (where {:email email}))))

(defn auth-user
  "return the logged in user if the password is correct, otherwise return false"
  [user password]
  (if user
    (if (bcrypt/check-password password (:pw_hash user)) user false)
    false))

(defn create-user
  [email password]
  (let [hashed (bcrypt/encrypt password)]
    (insert users 
            (values {:email email
                     :pw_hash hashed}))))
(defn create-session 
  [user]
  (let [randstr (rand/base64 50)]
    (insert sessions 
            (values {:id randstr
                     :user_id (:id user)}))))




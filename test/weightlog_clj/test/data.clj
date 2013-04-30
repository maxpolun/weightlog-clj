(ns weightlog-clj.test.data
    (:use clojure.test
          weightlog-clj.data
          korma.core
          korma.db))

(defn uuid [] (java.util.UUID/randomUUID))

(default-connection (create-db (postgres {:db "weightlog_test"
                                          :user "weightlog"
                                          :password "weightlog"
                                          :host "localhost"
                                          :port "5432"})))

(deftest test-users
  (testing "user-by-id"
    (do
      (with-transaction 
        (let [inserted-user (insert "users" 
                                    (values {:email "test@test.com"
                                             :pw_hash "fakehash"}))]
          (let [u (user-by-id (:id inserted-user))]
            (is (= (:email u) (:email inserted-user))
                (= (:pw_hash u) (:pw_hash inserted-user))))))
      (with-transaction
        (let [u (user-by-id (uuid))]
          (is (nil? u))))))
  (testing "user-by-email"
    (with-transaction
      (let [inserted-user (insert users
                                  (values {:email "test@test.com"
                                           :pw_hash "fakehash"}))
            u (user-by-email (:email inserted-user))]
        (is (= inserted-user u)))))
  (testing "create-user"
    (with-transaction 
      (let [u (create-user "test@test.com" "testpw")
            u2 (user-by-email "test@test.com")]
        (is (= u u2)))))
  (testing "auth-user"
    (do 
      (with-transaction 
        (let [email "test@test.com"
              password "testpw"
              u (create-user email password)
              result (auth-user u password)]
          (is (and result
                   (= u result)))))
      (with-transaction 
        (let [email "test@test.com"
              password "testpw"
              u (create-user email password)
              result (auth-user u "12345")]
          (is (not result))))))) 

(deftest test-sessions
  (testing "create-session"
    (with-transaction 
      (let [email "test@test.com"
            password "testpassword"
            u (create-user email password)
            s (create-session u)
            s2 (first (select sessions (where {:id (:id s)})))]
        (is (and (seq s)
                 (= s s2)
                 (= (:user_id s) (:id u))))))))

(ns weightlog-clj.test.handler
  (:use clojure.test
        ring.mock.request  
        weightlog-clj.handler)
  (:require  [clojure.data.json :as json]
             [weightlog-clj.data :as data]))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (= (json/read-str (:body response)) {"login" "/login"}))))
  
  (testing "login"
    (testing "success"
      (data/with-transaction 
        (data/create-user "test@test.com" "testpassword")
        (let [response (app (-> 
                             (request :post "/login") 
                             (body {:email "test@test.com"
                                    :password "testpassword"})))
              json-map (json/read-str (:body response))]
          (is (= 0 (count (:errors json-map)))))))
    (testing "failure"
      (data/with-transaction 
        (data/create-user "test@test.com" "testpassword")
        (let [response (app (-> 
                             (request :post "/login") 
                             (body {:email "test@test.com"
                                    :password "testpassword1"})))
              json-map (json/read-str (:body response)
                                      :value-fn (read-as-date #{"created_at"})]
          (is (some #("bad_login") (:errors json-map))))))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404))
      (is (= (json/read-str (:body response)) {"errors" ["not-found"]})))))

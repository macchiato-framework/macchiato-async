(ns macchiato.test.futures.core
  (:require [clojure.test :refer [deftest testing is async]]
            [macchiato.futures.core :refer [wrap-future detached-task task wait]]))


(def test-object
  (js-obj
    "sum" #(+ %1 %2)
    "sumLater" (fn [a b callback]
                 ; Expose an error-first callback interface
                 (js/setTimeout #(callback nil (+ a b)) 100)
                 callback)))

(identity 4)

(deftest verify-test-setup
  (is test-object)
  (is (= 7 (.sum test-object 3 4)))
  (is (fn? (.sumLater test-object 3 4 identity)))
  )

(deftest test-wrapping
  (testing "Wrapping a basic object"
    (let [wrapper (wrap-future test-object)]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sum")))
      (is (fn? (aget wrapper "sumLater")))))
  (testing "Wrapping with suffix"
    (let [wrapper (wrap-future test-object false "Future" false)]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sumFuture")))
      (is (fn? (aget wrapper "sumLaterFuture"))))))

(deftest test-task
  (let [wrapper (wrap-future test-object "Future")]
    (async done
      (task
        (fn []
          (try
            (is (= 37 (wait (.sumLaterFuture wrapper 19 18))))
            (is (= 42 (wait (.sumLaterFuture wrapper 22 20))))
            (is (= [2 5 10]
                   (map-indexed #(wait (.sumLaterFuture wrapper %2 (dec %1)))
                                [3 5 9])))
            (is (= [9 101 23]
                   (->> (map-indexed #(.sumLaterFuture wrapper %2 %1)
                                     [9 100 21])
                        (map wait))
                   ))
            (catch :default e
              (is false "Unexpected exception while evaluating task")
              (.error js/console "Error:" e))
            (finally
              (done))))))))
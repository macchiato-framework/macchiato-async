(ns macchiato.test.async.futures
  (:require [clojure.test :refer [deftest testing is async]]
            [macchiato.async.futures :refer [wrap-future detached-task task wait]]
            [macchiato.test.async.common :as common]))

(deftest test-wrapping
  (testing "Wrapping a basic object"
    (let [wrapper (wrap-future common/obj-with-fns)]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sum")))
      (is (fn? (aget wrapper "sumLater")))))
  (testing "Wrapping with suffix"
    (let [wrapper (wrap-future common/obj-with-fns "Future" false false)]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sumFuture")))
      (is (fn? (aget wrapper "sumLaterFuture"))))))

(deftest test-task
  (let [wrapper (wrap-future common/obj-with-fns "Future")]
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
                        ;; map-indexed above would return a collection of
                        ;; futures that we can then wait on.
                        (map wait))))
            (catch :default e
              (is false "Unexpected exception while evaluating task")
              (.error js/console "Error:" e))
            (finally
              (done))))))))
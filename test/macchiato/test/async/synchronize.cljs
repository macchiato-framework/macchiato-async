(ns macchiato.test.async.synchronize
  (:require [clojure.test :refer [deftest testing is async]]
            [cljs.nodejs :as node]
            [macchiato.async.synchronize :as sync]
            [macchiato.async.futures :refer [wrap-future detached-task task wait]]
            [macchiato.test.async.common :as common]))



(deftest test-await-defer
  (let [a (sync/await (.sumLater common/obj-with-fns 19 18 (sync/defer)))
        b (sync/await (.sumLater common/obj-with-fns 20 22 (sync/defer)))
        c (.sum common/obj-with-fns a b)]
    (is (= 79 c))
    (is (= 37 a))
    (is (= 42 b))))

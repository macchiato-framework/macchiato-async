(ns macchiato.test.async.synchronize
  (:require [cljs.test :refer-macros [deftest testing is]]
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


(deftest test-synchronize-single
  (let [synced (sync/synchronize common/obj-with-fns.sumLater)
        a      (synced 19 18)
        b      (synced 20 22)
        c      (.sum common/obj-with-fns a b)]
    (is (= 79 c))
    (is (= 37 a))
    (is (= 42 b))))

(deftest test-synchronize-invalid
  ; We can't just synchronize something that is not a function
  (is (thrown? js/Error (sync/synchronize common/obj-with-fns)))
  (is (thrown? js/Error (sync/synchronize "Hello world")))
  (is (thrown? js/Error (sync/synchronize 1))))


(deftest test-synchronize-by-name
  ; One function
  (let [sync-obj (sync/synchronize common/obj-with-fns "sumLater")
        a        (.sumLaterSync sync-obj 19 18)
        b        (.sumLaterSync sync-obj 20 22)
        c        (.sum common/obj-with-fns a b)]
    (is (= 79 c))
    (is (= 37 a))
    (is (= 42 b))
    ; Only sumLater was altered
    (is (= js/Function (type (aget sync-obj "sumLaterSync"))))
    (is (= js/Function (type (aget sync-obj "minusLater"))))
    (is (nil? (aget sync-obj "minusLaterSync"))))
  ; Mutiple functions
  (let [sync-obj (sync/synchronize common/obj-with-fns "sumLater" "minusLater")
        a        (.sumLaterSync sync-obj 19 18)
        b        (.sumLaterSync sync-obj 20 22)
        c        (.minusLaterSync sync-obj a b)]
    (is (= -5 c))
    (is (= 37 a))
    (is (= 42 b))
    (is (= js/Function (type (aget sync-obj "sumLaterSync"))))
    (is (= js/Function (type (aget sync-obj "minusLaterSync"))))))
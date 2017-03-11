(ns macchiato.test.futures.core
  (:require [clojure.test :refer [deftest testing is async]]
            [macchiato.futures.core :refer [wrap-future detached-task task wait]]))


;; Going to create a mock Javascript object which has two methods. One of them
;; returns immediately, the other one uses a callback. We'll then use this
;; object to test our futures in a controlled manner.
;;
;; We could just use one of the standard node classes, but that would obscure
;; what we're doing. I'd rather keep it as transparent as possible.
(def test-object
  (js-obj
    "sum" #(+ %1 %2)
    "sumLater" (fn [a b callback]
                 ;; Expose an error-first callback interface, which is what
                 ;; node would do (and what fibers/future expects).  That's
                 ;; why the callback's first parameter is nil.
                 ;;
                 ;; While this test function returns the callback function
                 ;; itself, that's just for my own testing purposes. It's not
                 ;; required or expected.
                 ;;
                 ;; Feel free to extend or randomize the timeout if you have
                 ;; any doubts this is being executed asynchronously.
                 (js/setTimeout #(callback nil (+ a b)) 100)
                 callback)))

(deftest verify-test-setup
  (is test-object)
  (is (= 7 (.sum test-object 3 4)))
  (is (fn? (.sumLater test-object 3 4 identity))))

(deftest test-wrapping
  (testing "Wrapping a basic object"
    (let [wrapper (wrap-future test-object)]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sum")))
      (is (fn? (aget wrapper "sumLater")))))
  (testing "Wrapping with suffix"
    (let [wrapper (wrap-future test-object "Future" false false)]
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
                        ;; map-indexed above would return a collection of
                        ;; futures that we can then wait on.
                        (map wait))))
            (catch :default e
              (is false "Unexpected exception while evaluating task")
              (.error js/console "Error:" e))
            (finally
              (done))))))))
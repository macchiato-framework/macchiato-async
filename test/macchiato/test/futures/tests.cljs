(ns macchiato.test.futures.tests
  (:require [macchiato.futures :as f]
            [macchiato.futures.futures  :as fut]
            [macchiato.futures.fibers   :as fib]
            [macchiato.futures.promises :as pro]
            [cljs.test :refer-macros [deftest testing is async]]))

(def future-test-obj
  (js-obj
   "sum" #(+ %1 %2)
   "sumLater" (fn [a b callback]
                (js/setTimeout #(callback nil (+ a b)) 100)
                callback)))

(deftest basics
  (let [f (fut/empty)
        p (pro/empty)
        fi (fib/make (constantly ::sentinel))
        trues  [(fut/isa? f) (pro/isa? p) (fib/isa? fi)]]
    (doseq [t trues]
      (is (true? t)))
    (testing "fut/isa?"
      (is (fut/isa? f))
      (is (not (fut/isa? p)))
      (is (not (fut/isa? fi))))
    (testing "pro/isa?"
      (is (pro/isa? p))
      (is (not (pro/isa? f)))
      (is (not (pro/isa? fi))))
    (testing "fib/isa?"
      (is (fib/isa? fi))
      (is (not (fib/isa? f)))
      (is (not (fib/isa? p))))))

(deftest futures-wrapping
  (testing "basic setup"
    (is future-test-obj)
    (is (= 7 (.sum future-test-obj 3 4)))
    (is (fn? (.sumLater future-test-obj 3 4 identity))))
  (testing "Wrapping a basic object"
    (let [wrapper (fut/wrap future-test-obj)]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sum")))
      (is (fn? (aget wrapper "sumLater")))))
  (testing "Wrapping with suffix"
    (let [wrapper (fut/wrap future-test-obj "Future")]
      (is (some? wrapper))
      (is (fn? (aget wrapper "sumFuture")))
      (is (fn? (aget wrapper "sumLaterFuture"))))))

(deftest futures-calling
  (let [wrapper (fut/wrap future-test-obj "Future")
        a (.sumLaterFuture wrapper 19 18)
        b (.sumLaterFuture wrapper 22 20)]
    (testing "isa?"
      (is (every? fut/isa? [a b])))
    (->> (try
           (testing "results"
             (is (= [37 42] [@(.sumLaterFuture wrapper 19 18)
                             @(.sumLaterFuture wrapper 22 20)]))
             (is (= [9 101 23] (->> [9 100 21]
                                    (map-indexed #(.sumLaterFuture wrapper %2 %1))
                                    doall (mapv deref)))))
           (catch :default e
             (is false "Unexpected exception while evaluating task")
             (.error js/console "Error:" e))
           (finally
             (done)))
         (fn []) fut/call (async done))))
  
(deftest multi-futures
  (let [wrapper (fut/wrap future-test-obj "Future")
        c (fut/combine (.sumLaterFuture wrapper 19 18)
                       (.sumLaterFuture wrapper 22 20))]
    (is (instance? fut/Futures c))
    (->> (try
           (testing "results"
             (is (= [37 42] @c)))
           (catch :default e
             (is false "Unexpected exception while evaluating task")
             (.error js/console "Error:" e))
           (finally
             (done)))
         fut/in
         (async done))))

(deftest promises-and-delivery
  (let [p1 (pro/empty)
        p2 (pro/empty)]
    (is (not (realized? p1)))
    (pro/deliver p1 ::sentinel)
    (is (realized? p1))
    (is (= ::sentinel @p1))
    (testing "to-future"
      (let [f1 (fut/to-future p1)
            f2 (fut/to-future p2)]
        (is (realized? f1))
        (is (realized? p1))
        (is (not (realized? f2)))
        (is (not (realized? p2)))
        (fut/deliver f2 ::sentinel)
        (is (realized? f2))
        (is (= ::sentinel @f2))))))

(deftest future-forgetting
  (let [p (pro/empty)]
    (->> (try
           (pro/deliver p ::sentinel)
           (catch :default e
             (is false "Unexpected exception while evaluating task")
             (.error js/console "Error:" e)))
         (fn []) fut/call-forget)
    (->> (try
           (is (= ::sentinel @p))
           (catch :default e
             (is false "Unexpected exception while evaluating task")
             (.error js/console "Error:" e))
           (finally
             (done)))
         fut/in (async done))))
    

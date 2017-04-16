(ns macchiato.test.async.common
  (:require [clojure.test :refer [deftest testing is async]]))

;; Going to create a mock Javascript object which has two methods. One of them
;; returns immediately, the other one uses a callback. We'll then use this
;; object to test our futures in a controlled manner.
;;
;; We could just use one of the standard node classes, but that would obscure
;; what we're doing. I'd rather keep it as transparent as possible.
(def obj-with-fns
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
  (is obj-with-fns)
  (is (= 7 (.sum obj-with-fns 3 4)))
  (is (fn? (.sumLater obj-with-fns 3 4 identity))))
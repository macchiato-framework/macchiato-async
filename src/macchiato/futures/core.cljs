(ns macchiato.futures.core
  (:require [cljs.nodejs :as node]))

(def Future (node/require "fibers/future"))
(defrecord ^{:doc "A collection of futures which must all complete"}
  Futures [futures])

(defn- js-apply
  "Applies a javascript function to a 'this' context and arguments
   args: [f this args]
   returns: result of calling f with args"
  [f this args]
  (.apply f this (to-array args)))

(defn wrap-future
  "Wraps an object or function in a future. Notice that by default we won't
  use any suffix.
  args: [o & [suffix? multi? stop?]]
    suffix: appends this string to any would-be overwritten methods
    multi:  if true, the callback takes multiple args, default false
    stop:   if true, will not copy member prototypes, default false
  returns: "
  ([o]
   (wrap-future o "" false false))
  ([o suffix]
   (.wrap Future o false suffix false))
  ([o suffix multi? stop?]
   (.wrap Future o multi? suffix stop?)))

(defn detached-task
  "Runs a function as a detached task.

  From the node-fibers documentation:

  Basically this is useful if you want to run a task in a future, you
  aren't interested in its return value, but if it throws you don't want the
  exception to be lost. If this fiber throws, an exception will be thrown to
  the event loop and node will probably fall down."
  [f]
  (->> f (.task Future) .detach))

(defn task
  "Runs a function as a task.
   args: [f] ; receives no arguments
   returns: Future"
  [f]
  (.task Future f))

(def wait
  "Waits on a future
   args: [future]
   returns: value of future"
  (memfn wait))

(defn wait+
  "Waits on several futures:
   args: [& fs]
   returns: vector of future values"
  [& fs]
  (when (seq fs)
    (if (next fs)
      (js-apply (memfn wait) Future fs)
      (wait (first fs)))
    (mapv deref fs)))

;; Give futures some clojurescript niceties

(extend-protocol IDeref
  Future
  (-deref [f]
    (wait f)))

(extend-type Futures
  Object
  (wait [self]
    (apply wait+ (.-futures self)))
  IDeref
  (-deref [self] (apply wait+ (:futures self))))

(defn combine
  "Combine several futures into one
   args: [& futs]
   returns: Futures, or nil if no futures provided"
  [& fs]
  (when (seq fs)
    (->Futures fs)))

  

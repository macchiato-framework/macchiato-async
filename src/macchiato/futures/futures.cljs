(ns macchiato.futures.futures
  (:refer-clojure :exclude [empty isa?])
  (:require [cljs.nodejs :as node]
            [macchiato.futures.util :refer [js-apply]])
  (:require-macros [macchiato.futures.futures]))

(def Future (js/require "fibers/future"))

(defprotocol IDeliverable
  (deliver [p v]
   "Delivers the supplied value to the promise, releasing any pending
    derefs. A subsequent call to deliver on a promise will have no effect.
    args: [promise value]"))

(defprotocol ToFuture
  (to-future [thing]
    "Attempts to cast thing to a Future or throws
     args: [thing] ; a Future or Promise
     returns: Future"))

(defrecord ^{:doc "A collection of futures/promises which must all complete"}
  Futures [futures])

(defn isa?
  "true if f is a Future
   args: [f]
   returns: bool"
  [f]
  (instance? Future f))

(defn empty
  "Creates an empty future, suitable for use with `deliver`
   args: []
   returns: Future"
  []
  (Future.))

(defn wrap
  "Wraps an object or function in a future. Notice that by default we won't
  use any suffix.
  args: [o & [suffix? multi? stop?]]
    suffix: appends this string to any would-be overwritten methods
    multi:  if true, the callback takes multiple args, default false
    stop:   if true, will not copy member prototypes, default false
  returns: "
  ([o]   (wrap o "" false false))
  ([o s] (wrap o s  false false))
  ([o suffix multi? stop?]
   (.wrap Future o multi? suffix stop?)))

(defn call
  "Runs a function of 0 args in a future
   args: [f]
   returns: Future"
  [f]
  (.task Future f))

(def forget
  "From the node-fibers documentation:

  Basically this is useful if you want to run a task in a future, you
  aren't interested in its return value, but if it throws you don't want the
  exception to be lost. If this fiber throws, an exception will be thrown to
  the event loop and node will probably fall down.
  args: [fiber]"
  (memfn detach))

(def call-forget
  "Runs a function as a detached task.

  From the node-fibers documentation:

  Basically this is useful if you want to run a task in a future, you
  aren't interested in its return value, but if it throws you don't want the
  exception to be lost. If this fiber throws, an exception will be thrown to
  the event loop and node will probably fall down."
  (comp forget call))

(defn combine
  "Combine several futures into one
   args: [& futs]
   returns: Futures, or nil if no futures provided"
  [& fs]
  (when (seq fs)
    (->Futures (map to-future fs))))

(extend-type Future
  ToFuture
  (to-future [f] f)
  IDeref
  (-deref [f] (.wait f))
  IPending
  (-realized? [f] (.isResolved f))
  IDeliverable
  (deliver [f v] (.return f v)))
  
(extend-type Futures
  ToFuture
  (to-future [f] (call #(deref (:futures f))))
  IDeref
  (-deref [f]
    (let [futs (:futures f)]
      (js-apply (.-wait Future) Future futs)
      (mapv (memfn get) futs)))
  IPending
  (-realized? [f] (every? realized? (:futures f))))


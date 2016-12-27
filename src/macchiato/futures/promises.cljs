(ns macchiato.futures.promises
  (:refer-clojure :exclude [isa? empty])
  (:require [macchiato.futures.futures :as future]
            [cljs.nodejs :as node]))

(defrecord ^{:doc "A promise based on Futures"}
  Promise [future])

(defn isa?
  "true if p is a Promise
   args: [p]
   returns: bool"
  [p]
  (instance? Promise p))

(defn empty
  "Returns a promise object that can be read with deref/@, and set,
   once only, with deliver. Calls to deref/@ prior to delivery will
   block, unless the variant of deref with timeout is used. All
   subsequent derefs will return the same delivered value without
   blocking. See also - realized?."
  []
  (->Promise (future/empty)))

(extend-type Promise
  IDeref
  (-deref [p] (deref (:future p)))
  IPending
  (-realized? [p] (.isResolved (:future p)))
  future/IDeliverable
  (deliver [p v]
    (future/deliver (:future p) v))
  future/ToFuture
  (to-future [p] (:future p)))

(def deliver 
  "Delivers the supplied value to the promise, releasing any pending
   derefs. A subsequent call to deliver on a promise will have no effect.
   args: [promise value]"
  future/deliver)


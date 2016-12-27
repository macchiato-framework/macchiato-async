(ns macchiato.futures
  (:require [macchiato.futures.futures  :as fut]
            [macchiato.futures.fibers   :as fib]
            [macchiato.futures.promises :as pro]))

(def future-call
  "Runs a function of 0 args in a future. See also `future`
   args: [f]
   returns: Future"
  fut/call)

(defmacro future
  "Runs the exprs in a future. See also `future-call`
   args: [& exprs]"
  [& exprs]
  `(fut/in ~@exprs))

(def promise
  "Returns a promise object that can be read with deref/@, and set,
   once only, with deliver. Calls to deref/@ prior to delivery will
   block, unless the variant of deref with timeout is used. All
   subsequent derefs will return the same delivered value without
   blocking. See also - realized?."
  pro/empty)

(def deliver
  "Delivers the supplied value to the promise, releasing any pending
   derefs. A subsequent call to deliver on a promise will have no effect."
  pro/deliver)

(def fiber?
  "true if f is a Fiber
   args: [p]
   returns: bool"
  fib/isa?)

(def future?
  "true if f is a Future
   args: [p]
   returns: bool"
  fut/isa?)

(def promise?
  "true if p is a Promise
   args: [p]
   returns: bool"
  pro/isa?)

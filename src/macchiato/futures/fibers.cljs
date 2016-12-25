(ns macchiato.futures.fibers
  (:refer-clojure :exclude [isa? empty])
  (:require [cljs.nodejs :as node])
  (:require-macros [macchiato.futures.fibers]))

(def Fiber (node/require "fibers"))

(defn isa?
  "true if f is a Fiber
   args: [f]
   returns: bool"
  [f]
  (instance? Fiber f))

(defn current
  "Returns the current Fiber or nil
   args: []"
  []
  (.-current Fiber))

(defn current!
  "Returns the current Fiber or throws
   args: [err-msg err-info] ; for ex-info in case of failure
   returns: Fiber
   throws: if we're not in a fiber"
  [err-msg err-info]
  (or (current)
      (throw (ex-info err-msg err-info))))

(defn make
  "Makes a Fiber object out of a function of 0 args
   args: [fn]
   returns: Fiber"
  [f]
  (Fiber. f))

(def run
  "Runs a Fiber object, returns instantly
   args: [fiber]"
  (memfn run))

(def run-in
  "Runs a function of 0 args in a new fiber
   args: [f]"
  (comp run make))

(def cancel
  "Attempts to cancel a fiber by making calls to yield throw ad infinitum.
   args: [fiber]"
  (memfn reset))
  
(def throw-in
  "Makes the current/next call to yield by the fiber throw
   args: [fiber exception]"
  (memfn throwInto))

(defn yield
  "Releases control of the current fiber, optionally presenting a result
   args: [] [val]"
  [& [v :as vs]]
  (current! "yield must be called from within a fiber" {})
  (if (seq vs)
    (.yield Fiber v)
    (.yield Fiber)))


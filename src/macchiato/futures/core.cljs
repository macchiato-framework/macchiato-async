(ns macchiato.futures.core)

(def Future (js/require "fibers/future"))

(defn wrap-future
  "Wraps an object or function in a future. Notice that by default we won't
  use any suffix."
  ([o]
   (wrap-future o false "" false))
  ([o suffix]
   (.wrap Future o false suffix false))
  ([o multi? suffix stop?]
   (.wrap Future o multi? suffix stop?)))

(defn detached-task
  "Runs a function as a detached task.

  From the node-fibers documentation:

  Basically this is useful if you want to run a task in a future, you
  aren't interested in its return value, but if it throws you don't want the exception to be
  lost. If this fiber throws, an exception will be thrown to the event loop and node will
  probably fall down."
  [f]
  (->> f (.task Future) .detach))

(defn task
  "Runs a function as a task."
  [f]
  (.task Future f))


(defn wait
  "Waits on a future. Surprise, surprise."
  [f]
  (.wait f))